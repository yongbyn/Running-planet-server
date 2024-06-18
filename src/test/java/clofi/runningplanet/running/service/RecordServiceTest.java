package clofi.runningplanet.running.service;

import static clofi.runningplanet.common.utils.TimeUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.domain.Role;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.service.CrewService;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.RecordFindAllResponse;
import clofi.runningplanet.running.dto.RecordFindCurrentResponse;
import clofi.runningplanet.running.dto.RecordFindResponse;
import clofi.runningplanet.running.dto.RecordSaveRequest;
import clofi.runningplanet.running.dto.RunningStatusFindAllResponse;
import clofi.runningplanet.running.repository.CoordinateRepository;
import clofi.runningplanet.running.repository.RecordRepository;

@SpringBootTest
class RecordServiceTest {
	@Autowired
	RecordService recordService;

	@Autowired
	RecordRepository recordRepository;

	@Autowired
	CoordinateRepository coordinateRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CrewService crewService;

	@SpyBean
	private AuditingHandler auditingHandler;

	@Autowired
	private CrewRepository crewRepository;

	@Autowired
	private CrewMemberRepository crewMemberRepository;

	@AfterEach
	void tearDown() {
		crewMemberRepository.deleteAllInBatch();
		crewRepository.deleteAllInBatch();
		coordinateRepository.deleteAllInBatch();
		recordRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		auditingHandler.setDateTimeProvider(null);
	}

	@DisplayName("운동, 좌표 정보로 운동 기록을 생성할 수 있다.")
	@Test
	void saveRecord() {
		// given
		RecordSaveRequest recordSaveRequest = new RecordSaveRequest(
			100.23,
			200.23,
			630,
			1.23,
			300,
			new RecordSaveRequest.AvgPace(
				8,
				20
			),
			false
		);

		// when
		Member member = memberRepository.save(createMember("감자"));
		Record savedRecord = recordService.save(recordSaveRequest, member.getId());

		// then
		assertThat(savedRecord.getId()).isNotNull();
		assertThat(savedRecord)
			.extracting("runTime", "runDistance", "calories", "avgPace", "endTime")
			.contains(630, 1.23, 300, 500, null);

		Optional<Coordinate> savedCoordinate = coordinateRepository.findByRecord(savedRecord);
		assertThat(savedCoordinate).isPresent();
		assertThat(savedCoordinate.get())
			.extracting("latitude", "longitude")
			.contains(100.23, 200.23);
	}

	@DisplayName("종료되지 않은 기록을 업데이트하고, 좌표를 추가할 수 있다.")
	@Test
	void updateRecordAndAddCoordinate() {
		// given
		RecordSaveRequest recordSaveRequest1 = new RecordSaveRequest(
			100.23,
			200.23,
			630,
			1.23,
			300,
			new RecordSaveRequest.AvgPace(
				8,
				20
			),
			false
		);

		Member member = memberRepository.save(createMember("감자"));
		recordService.save(recordSaveRequest1, member.getId());

		RecordSaveRequest recordSaveRequest2 = new RecordSaveRequest(
			200.23,
			300.23,
			900,
			2.0,
			400,
			new RecordSaveRequest.AvgPace(
				10,
				30
			),
			true
		);

		// when
		Record updatedRecord = recordService.save(recordSaveRequest2, member.getId());

		// then
		assertThat(updatedRecord.getId()).isNotNull();
		assertThat(updatedRecord)
			.extracting("runTime", "runDistance", "calories", "avgPace")
			.contains(900, 2.0, 400, 630);
		assertThat(updatedRecord.getEndTime()).isNotNull();

		List<Coordinate> savedCoordinates = coordinateRepository.findAllByRecord(updatedRecord);
		assertThat(savedCoordinates).hasSize(2)
			.extracting("latitude", "longitude")
			.containsExactlyInAnyOrder(
				tuple(100.23, 200.23),
				tuple(200.23, 300.23)
			);
	}

	@DisplayName("운동 종료 시 회원의 운동 정보가 업데이트된다.")
	@Test
	void updateMemberStatistics() {
		Member member = memberRepository.save(createMember("회원1"));
		RecordSaveRequest recordSaveRequest1 = new RecordSaveRequest(1, 1, 600, 1, 1,
			new RecordSaveRequest.AvgPace(10, 0), true
		);
		RecordSaveRequest recordSaveRequest2 = new RecordSaveRequest(2, 2, 1200, 2, 2,
			new RecordSaveRequest.AvgPace(10, 0), true
		);
		recordService.save(recordSaveRequest1, member.getId());
		recordService.save(recordSaveRequest2, member.getId());

		// when
		Member savedMember = memberRepository.findById(member.getId()).get();

		// then
		assertThat(savedMember)
			.extracting("avgPace", "avgDistance", "totalDistance")
			.contains(600, 1.5, 3.0);
	}

	@DisplayName("year, month 로 운동 기록을 조회할 수 있다.")
	@Test
	void findAllRecordsByYearAndMonth() {
		// given
		Member member = memberRepository.save(createMember("감자"));

		LocalDateTime createdDateTime1 = getEndOfDay(LocalDate.of(2024, 1, 31));
		setAuditingHandlerDateTime(createdDateTime1);
		recordRepository.save(createRecord(member, true));

		LocalDateTime createdDateTime2 = getStartOfDay(LocalDate.of(2024, 2, 1));
		setAuditingHandlerDateTime(createdDateTime2);
		Record record2 = recordRepository.save(createRecord(member, true));

		LocalDateTime createdDateTime3 = getEndOfDay(LocalDate.of(2024, 2, 29));
		setAuditingHandlerDateTime(createdDateTime3);
		Record record3 = recordRepository.save(createRecord(member, true));

		LocalDateTime createdDateTime4 = getStartOfDay(LocalDate.of(2024, 3, 1));
		setAuditingHandlerDateTime(createdDateTime4);
		recordRepository.save(createRecord(member, true));

		int year = 2024;
		int month = 2;

		// when
		List<RecordFindAllResponse> response = recordService.findAll(year, month, member.getId());

		assertThat(response).hasSize(2)
			.extracting("id", "day")
			.containsExactlyInAnyOrder(
				tuple(record2.getId(), 1),
				tuple(record3.getId(), 29)
			);
	}

	@DisplayName("운동 아이디로 운동 기록을 조회할 수 있다.")
	@Test
	void findRecord() {
		// given
		Member member = memberRepository.save(createMember("감자"));

		Record record = createRecord(member, 65, 1.00, 3665, 300, true);
		Coordinate coordinate1 = createCoordinate(record, 10.00, 20.00);
		Coordinate coordinate2 = createCoordinate(record, 20.00, 30.00);
		Record savedRecord = recordRepository.save(record);
		coordinateRepository.save(coordinate1);
		coordinateRepository.save(coordinate2);

		Long recordId = savedRecord.getId();

		// when
		RecordFindResponse response = recordService.find(recordId, member.getId());

		// then
		assertThat(response.id()).isNotNull();
		assertThat(response.avgPace())
			.extracting("min", "sec")
			.contains(1, 5);
		assertThat(response.runTime())
			.extracting("hour", "min", "sec")
			.contains(1, 1, 5);
		assertThat(response)
			.extracting("runDistance", "calories")
			.contains(1.00, 300);
		assertThat(response.endTime()).isNotNull();
		assertThat(response.coordinateResponses()).hasSize(2)
			.extracting("latitude", "longitude")
			.containsExactlyInAnyOrder(
				tuple(10.00, 20.00),
				tuple(20.00, 30.00)
			);
	}

	@DisplayName("종료되지 않은 운동 기록은 조회할 수 없다.")
	@Test
	void findUnfinishedRecord() {
		// given
		Member member = memberRepository.save(createMember("감자"));
		Record record = recordRepository.save(createRecord(member, false));

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> recordService.find(record.getId(), member.getId()))
			.withMessage("운동 기록을 찾을 수 없습니다.");
	}

	@DisplayName("운동 아이디가 존재하지 않거나, 자신의 운동 기록이 아니면 예외가 발생한다.")
	@Test
	void findRecordByInvalidRequest() {
		// given
		Member member = memberRepository.save(createMember("감자"));
		Member member2 = memberRepository.save(createMember("감자"));

		Record record = createRecord(member, true);
		Coordinate coordinate1 = createCoordinate(record);
		Coordinate coordinate2 = createCoordinate(record);
		Record savedRecord = recordRepository.save(record);
		coordinateRepository.save(coordinate1);
		coordinateRepository.save(coordinate2);

		Long recordId = savedRecord.getId();

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> recordService.find(recordId + 1, member.getId()))
			.withMessage("운동 기록을 찾을 수 없습니다.");
		assertThatIllegalArgumentException()
			.isThrownBy(() -> recordService.find(recordId, member2.getId()))
			.withMessage("운동 기록을 찾을 수 없습니다.");
	}

	@DisplayName("현재 운동 정보를 조회할 수 있다.")
	@Test
	void findCurrentRecord() {
		// given
		Member member = memberRepository.save(createMember("감자"));

		Record record = createRecord(member, 65, 1.00, 3665, 300, false);
		Coordinate coordinate1 = createCoordinate(record, 10.00, 20.00);
		Coordinate coordinate2 = createCoordinate(record, 20.00, 30.00);
		recordRepository.save(record);
		coordinateRepository.save(coordinate1);
		coordinateRepository.save(coordinate2);

		// when
		RecordFindCurrentResponse response = recordService.findCurrentRecord(member.getId());

		// then
		assertThat(response.id()).isNotNull();
		assertThat(response.avgPace())
			.extracting("min", "sec")
			.contains(1, 5);
		assertThat(response.runTime())
			.extracting("hour", "min", "sec")
			.contains(1, 1, 5);
		assertThat(response)
			.extracting("runDistance", "calories", "latitude", "longitude")
			.contains(1.00, 300, 20.00, 30.00);
	}

	@DisplayName("현재 운동 조회 시 종료되지 않은 운동 기록이 없으면 null이 반환된다.")
	@Test
	void findCurrentWorkoutRecordWhenNoneUnfinished() {
		// given
		Member member = memberRepository.save(createMember("감자"));

		Record record = createRecord(member, true);
		Coordinate coordinate = createCoordinate(record);
		recordRepository.save(record);
		coordinateRepository.save(coordinate);

		// when
		RecordFindCurrentResponse response = recordService.findCurrentRecord(member.getId());

		// then
		assertThat(response).isNull();
	}

	@DisplayName("회원과 크루 정보로 운동 현황 목록 조회를 할 수 있다.")
	@Test
	void findAllRunningStatus() {
		Member member1 = memberRepository.save(createMember("회원1"));
		Member member2 = memberRepository.save(createMember("회원2"));
		Member member3 = memberRepository.save(createMember("회원3"));
		Member member4 = memberRepository.save(createMember("회원4"));
		Crew crew = crewRepository.save(createCrew(member1.getId()));
		crewMemberRepository.save(CrewMember.builder().crew(crew).member(member1).role(Role.LEADER).build());
		crewMemberRepository.save(CrewMember.builder().crew(crew).member(member2).role(Role.MEMBER).build());
		crewMemberRepository.save(CrewMember.builder().crew(crew).member(member3).role(Role.MEMBER).build());
		crewMemberRepository.save(CrewMember.builder().crew(crew).member(member4).role(Role.MEMBER).build());

		LocalDate today = LocalDate.now();

		LocalDateTime endOfYesterday = getEndOfDay(today.minusDays(1));
		setAuditingHandlerDateTime(endOfYesterday);
		recordRepository.save(createRecord(member1, 1.0, 100, true));

		LocalDateTime startOfToday = getStartOfDay(today);
		setAuditingHandlerDateTime(startOfToday);
		recordRepository.save(createRecord(member2, 2.0, 200, true));
		recordRepository.save(createRecord(member1, 2.0, 200, true));

		LocalDateTime endOfToday = getEndOfDay(today);
		setAuditingHandlerDateTime(endOfToday);
		recordRepository.save(createRecord(member1, 3.0, 300, true));
		recordRepository.save(createRecord(member3, 3.0, 300, false));

		LocalDateTime startOfTomorrow = getStartOfDay(today.plusDays(1));
		setAuditingHandlerDateTime(startOfTomorrow);
		recordRepository.save(createRecord(member1, 4.0, 400, true));

		// when
		List<RunningStatusFindAllResponse> response = recordService.findAllRunningStatus(member1.getId(), crew.getId());

		// then
		assertThat(response).hasSize(4)
			.extracting("memberId", "profileImg", "runTime", "runDistance", "isEnd", "canCheer")
			.containsExactly(
				tuple(member3.getId(), "defaultProfileImg", 300, 3.0, false, true),
				tuple(member1.getId(), "defaultProfileImg", 500, 5.0, true, true),
				tuple(member2.getId(), "defaultProfileImg", 200, 2.0, true, true),
				tuple(member4.getId(), "defaultProfileImg", 0, 0.0, true, false)
			);
	}

	private Member createMember(String nickname) {
		return Member.builder()
			.nickname(nickname)
			.profileImg("defaultProfileImg")
			.age(3)
			.gender(Gender.MALE)
			.avgPace(600)
			.totalDistance(3000)
			.build();
	}

	private Record createRecord(Member member, boolean isEnd) {
		return this.createRecord(member, 1, 1, 1, 1, isEnd);
	}

	private Record createRecord(Member member, double runDistance, int runTime, boolean isEnd) {
		return this.createRecord(member, 1, runDistance, runTime, 1, isEnd);
	}

	private Record createRecord(Member member, int avgPace, double runDistance, int runTime, int calories,
		boolean isEnd) {
		return Record.builder()
			.member(member)
			.avgPace(avgPace)
			.runDistance(runDistance)
			.runTime(runTime)
			.calories(calories)
			.isEnd(isEnd)
			.build();
	}

	private Coordinate createCoordinate(Record record) {
		return this.createCoordinate(record, 1.0, 1.0);
	}

	private Coordinate createCoordinate(Record record, double latitude, double longitude) {
		return Coordinate.builder()
			.record(record)
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}

	private Crew createCrew(Long leaderId) {
		return new Crew(leaderId, "crew1", 5, Category.RUNNING, ApprovalType.AUTO, "crew1", 1, 1);
	}

	private void setAuditingHandlerDateTime(LocalDateTime localDateTime) {
		auditingHandler.setDateTimeProvider(() -> Optional.of(localDateTime));
	}

}
