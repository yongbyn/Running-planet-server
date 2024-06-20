package clofi.runningplanet.mission.service;

import static clofi.runningplanet.common.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.common.exception.ForbiddenException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.dto.response.GetCrewMissionResDto;
import clofi.runningplanet.mission.repository.CrewMissionRepository;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.repository.RecordRepository;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

	@Mock
	private CrewMissionRepository crewMissionRepository;

	@Mock
	private CrewRepository crewRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CrewMemberRepository crewMemberRepository;

	@Mock
	private RecordRepository recordRepository;

	@InjectMocks
	private MissionService missionService;

	@DisplayName("크루 미션 목록 조회 성공")
	@Test
	void successGetAllCrewMission() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		List<CrewMission> crewMissionList = crewMissionList();
		List<Record> todayRecordList = createTodayRecordList();

		given(crewRepository.existsById(anyLong()))
			.willReturn(true);
		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findAllByCrewIdAndMemberIdAndToday(anyLong(), anyLong(), any(LocalDateTime.class),
			any(
				LocalDateTime.class)))
			.willReturn(crewMissionList);
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class), any(
			LocalDateTime.class)))
			.willReturn(todayRecordList);

		//when
		CrewMissionListDto result = missionService.getCrewMission(crewId, memberId);

		//then
		List<GetCrewMissionResDto> getCrewMissionResDtos = List.of(
			new GetCrewMissionResDto(1L, MissionType.DISTANCE, 100, true),
			new GetCrewMissionResDto(2L, MissionType.DURATION, (double)1800 / 3600 * 100, false)
		);

		CrewMissionListDto expected = new CrewMissionListDto(getCrewMissionResDtos);

		assertThat(result).isEqualTo(expected);
	}

	@DisplayName("일일 운동 기록이 없을 경우 progress 0 반환")
	@Test
	void successGetAllCrewMissionNotRecord() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		List<CrewMission> crewMissionList = crewMissionListNotComplete();

		given(crewRepository.existsById(anyLong()))
			.willReturn(true);
		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findAllByCrewIdAndMemberIdAndToday(anyLong(), anyLong(), any(LocalDateTime.class),
			any(
				LocalDateTime.class)))
			.willReturn(crewMissionList);
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class), any(
			LocalDateTime.class)))
			.willReturn(Collections.emptyList());

		//when
		CrewMissionListDto result = missionService.getCrewMission(crewId, memberId);

		//then
		List<GetCrewMissionResDto> getCrewMissionResDtos = List.of(
			new GetCrewMissionResDto(1L, MissionType.DISTANCE, 0, false),
			new GetCrewMissionResDto(2L, MissionType.DURATION, 0, false)
		);

		CrewMissionListDto expected = new CrewMissionListDto(getCrewMissionResDtos);

		assertThat(result).isEqualTo(expected);
	}

	@DisplayName("미션 조회 시 크루가 존재하지 않는 경우 예외 발생")
	@Test
	void failGetAllCrewMissionByNotFoundCrew() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		given(crewRepository.existsById(anyLong()))
			.willReturn(false);

		//when
		//then
		assertThatThrownBy(() -> missionService.getCrewMission(crewId, memberId))
			.isInstanceOf(NotFoundException.class);
	}

	@DisplayName("미션 조회 시 사용자가 존재하지 않는 경우 예외 발생")
	@Test
	void failGetAllCrewMissionByNotFoundMember() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		given(crewRepository.existsById(anyLong()))
			.willReturn(true);
		given(memberRepository.existsById(anyLong()))
			.willReturn(false);

		//when
		//then
		assertThatThrownBy(() -> missionService.getCrewMission(crewId, memberId))
			.isInstanceOf(NotFoundException.class);
	}

	@DisplayName("미션 조회 시 소속 크루가 아닌 경우 예외 발생")
	@Test
	void failGetAllCrewMissionByNotInCrew() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		given(crewRepository.existsById(anyLong()))
			.willReturn(true);
		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(false);

		//when
		//then
		assertThatThrownBy(() -> missionService.getCrewMission(crewId, memberId))
			.isInstanceOf(ForbiddenException.class);
	}

	@DisplayName("크루 미션 성공")
	@Test
	void successCrewMission() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		Crew crew = createCrew();
		CrewMission mission = createDistanceCrewMission();
		List<Record> todayRecordList = createTodayRecordList();

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findById(anyLong()))
			.willReturn(Optional.of(mission));
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class),
			any(LocalDateTime.class)))
			.willReturn(todayRecordList);
		given(crewRepository.findById(anyLong()))
			.willReturn(Optional.of(crew));

		//when
		//then
		assertDoesNotThrow(() -> missionService.successMission(crewId, missionId, memberId));
	}

	@DisplayName("사용자 정보가 없는 사용자가 미션 성공 시 예외 발생")
	@Test
	void failCrewMissionByNotFoundMember() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		given(memberRepository.existsById(anyLong()))
			.willReturn(false);

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(NotFoundException.class);
	}

	@DisplayName("크루에 속해있지 않은 크루원이 미션 성공 시 예외 발생")
	@Test
	void failCrewMissionNotInCrew() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(false);

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(ForbiddenException.class);
	}

	@DisplayName("미션 성공 시 해당 미션이 없는 경우 예외 발생")
	@Test
	void failCrewMissionNotFoundMission() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(NotFoundException.class);
	}

	@DisplayName("미션 성공 시 크루가 없는 경우 예외 발생")
	@Test
	void failCrewMissionNotFoundCrew() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		CrewMission mission = createDistanceCrewMission();
		List<Record> todayRecordList = createTodayRecordList();

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findById(anyLong()))
			.willReturn(Optional.of(mission));
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class),
			any(LocalDateTime.class)))
			.willReturn(todayRecordList);
		given(crewRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(NotFoundException.class);
	}

	@DisplayName("이미 성공한 미션을 다시 성공 요청 시 예외 발생")
	@Test
	void failCrewMissionReSuccess() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		CrewMission mission = createCompleteDistanceCrewMission();

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findById(anyLong()))
			.willReturn(Optional.of(mission));

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("성공 조건 미 충족 시 예외 발생")
	@Test
	void failCrewMission() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;
		Long missionId = 1L;

		CrewMission mission = createDurationCrewMission();
		List<Record> todayRecordList = createTodayRecordList();

		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findById(anyLong()))
			.willReturn(Optional.of(mission));
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class),
			any(LocalDateTime.class)))
			.willReturn(todayRecordList);

		//when
		//then
		assertThatThrownBy(() -> missionService.successMission(crewId, missionId, memberId))
			.isInstanceOf(ConflictException.class);
	}
}
