package clofi.runningplanet.running.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.RecordFindResponse;
import clofi.runningplanet.running.dto.RecordSaveRequest;
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

	@AfterEach
	void tearDown() {
		coordinateRepository.deleteAllInBatch();
		recordRepository.deleteAllInBatch();
	}

	@DisplayName("운동, 좌표 정보로 운동 기록을 생성할 수 있다.")
	@Test
	void saveRecord() {
		//TODO: 회원 기능 구현 후 로직 추가 작성

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
		Record savedRecord = recordService.save(recordSaveRequest);

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

	@DisplayName("운동 아이디로 운동 기록을 조회할 수 있다.")
	@Test
	void findRecord() {
		// given
		LocalDateTime endTime = LocalDateTime.now();
		Record record = createRecord(65, 1.00, 3665, 300, endTime);
		Coordinate coordinate1 = createCoordinate(record, 10.00, 20.00);
		Coordinate coordinate2 = createCoordinate(record, 20.00, 30.00);
		Record savedRecord = recordRepository.save(record);
		coordinateRepository.save(coordinate1);
		coordinateRepository.save(coordinate2);

		Long recordId = savedRecord.getId();

		// when
		RecordFindResponse response = recordService.find(recordId);

		// then
		assertThat(response.id()).isNotNull();
		assertThat(response.avgPace())
			.extracting("min", "sec")
			.contains(1, 5);
		assertThat(response.runTime())
			.extracting("hour", "min", "sec")
			.contains(1, 1, 5);
		assertThat(response)
			.extracting("runDistance", "calories", "endTime")
			.contains(1.00, 300, endTime);
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
		Record record = createRecord(65, 1.00, 3665, 300, null);
		Record saved = recordRepository.save(record);
		Long recordId = saved.getId();

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> recordService.find(recordId))
			.withMessage("운동 기록을 찾을 수 없습니다.");
	}

	private Record createRecord(int avgPace, double runDistance, int runTime, int calories, LocalDateTime endTime) {
		return Record.builder()
			.avgPace(avgPace)
			.runDistance(runDistance)
			.runTime(runTime)
			.calories(calories)
			.endTime(endTime)
			.build();
	}

	private Coordinate createCoordinate(Record record, double latitude, double longitude) {
		return Coordinate.builder()
			.record(record)
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}
}
