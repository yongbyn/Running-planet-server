package clofi.runningplanet.running.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.RecordFindResponse;
import clofi.runningplanet.running.dto.RecordSaveRequest;
import clofi.runningplanet.running.repository.CoordinateRepository;
import clofi.runningplanet.running.repository.RecordRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecordService {
	private final RecordRepository recordRepository;
	private final CoordinateRepository coordinateRepository;

	@Transactional
	public Record save(RecordSaveRequest request) {
		//TODO: member 조회 및 최근 기록 조회, 종료되지 않은 기록이 있으면 업데이트하기
		Record record = Record.builder()
			.runTime(request.runTime())
			.runDistance(request.runDistance())
			.calories(request.calories())
			.avgPace(request.avgPace().min() * 60 + request.avgPace().sec())
			.build();

		Record savedRecord = recordRepository.save(record);

		Coordinate coordinate = Coordinate.builder()
			.record(record)
			.latitude(request.latitude())
			.longitude(request.longitude())
			.build();

		coordinateRepository.save(coordinate);

		return savedRecord;
	}

	public RecordFindResponse find(Long recordId) {
		//TODO: 회원 정보도 조건에 추가하기
		Record record = recordRepository.findByIdAndEndTimeIsNotNull(recordId)
			.orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));

		List<Coordinate> coordinates = coordinateRepository.findAllByRecord(record);

		return new RecordFindResponse(record, coordinates);
	}
}
