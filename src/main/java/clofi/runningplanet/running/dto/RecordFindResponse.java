package clofi.runningplanet.running.dto;

import java.time.LocalDateTime;
import java.util.List;

import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;

public record RecordFindResponse(
	Long id,
	AvgPace avgPace,
	RunTime runTime,
	double runDistance,
	List<CoordinateResponse> coordinateResponses,
	int calories,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public RecordFindResponse(Record record, List<Coordinate> coordinates) {
		this(record.getId(),
			new AvgPace(record.getAvgPace()),
			new RunTime(record.getRunTime()),
			record.getRunDistance(),
			coordinates.stream().map(CoordinateResponse::new).toList(),
			record.getCalories(),
			record.getCreatedAt(),
			record.getEndTime()
		);
	}

	public record AvgPace(
		int min,
		int sec
	) {
		public AvgPace(int avgPace) {
			this(avgPace / 60, avgPace % 60);
		}
	}

	public record RunTime(
		int hour,
		int min,
		int sec
	) {
		public RunTime(int runTime) {
			this(runTime / (60 * 60), (runTime / 60) % (60 * 60), runTime % 60);
		}
	}
}
