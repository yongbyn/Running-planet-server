package clofi.runningplanet.mission.domain.vo;

import java.util.List;

import clofi.runningplanet.running.domain.Record;
import lombok.Getter;

@Getter
public class TodayRecords {
	private final double totalDistance;
	private final int totalDuration;

	public TodayRecords(List<Record> records) {
		this.totalDistance = records.stream().mapToDouble(Record::getRunDistance).sum();
		this.totalDuration = records.stream().mapToInt(Record::getRunTime).sum();
	}

	public TodayRecords(double totalDistance, int totalDuration) {
		this.totalDistance = totalDistance;
		this.totalDuration = totalDuration;
	}
}
