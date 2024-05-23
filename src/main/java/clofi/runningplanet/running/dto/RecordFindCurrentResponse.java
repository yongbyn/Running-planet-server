package clofi.runningplanet.running.dto;

import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;

public record RecordFindCurrentResponse(
	Long id,
	AvgPaceResponse avgPace,
	RunTimeResponse runTime,
	double runDistance,
	int calories,
	double latitude,
	double longitude
) {
	public RecordFindCurrentResponse(Record record, Coordinate coordinate) {
		this(record.getId(),
			new AvgPaceResponse(record.getAvgPace()),
			new RunTimeResponse(record.getRunTime()),
			record.getRunDistance(),
			record.getCalories(),
			coordinate.getLatitude(),
			coordinate.getLongitude()
		);
	}
}
