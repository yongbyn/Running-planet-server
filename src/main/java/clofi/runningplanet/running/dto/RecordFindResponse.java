package clofi.runningplanet.running.dto;

import java.time.LocalDateTime;
import java.util.List;

import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;

public record RecordFindResponse(
	Long id,
	AvgPaceResponse avgPace,
	RunTimeResponse runTime,
	double runDistance,
	List<CoordinateResponse> coordinateResponses,
	int calories,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public RecordFindResponse(Record record, List<Coordinate> coordinates) {
		this(record.getId(),
			new AvgPaceResponse(record.getAvgPace()),
			new RunTimeResponse(record.getRunTime()),
			record.getRunDistance(),
			coordinates.stream().map(CoordinateResponse::new).toList(),
			record.getCalories(),
			record.getCreatedAt(),
			record.getEndTime()
		);
	}

}
