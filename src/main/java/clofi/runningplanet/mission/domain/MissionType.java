package clofi.runningplanet.mission.domain;

import clofi.runningplanet.mission.domain.vo.TodayRecords;
import lombok.Getter;

@Getter
public enum MissionType {
	DISTANCE("1km 달리기", 1000),
	DURATION("1시간 달리기", 3600);

	private final String content;
	private final int requiredScore;

	MissionType(String content, int requiredScore) {
		this.content = content;
		this.requiredScore = requiredScore;
	}

	public boolean isComplete(TodayRecords todayRecords) {
		return switch (this) {
			case DISTANCE -> todayRecords.getTotalDistance() >= requiredScore;
			case DURATION -> todayRecords.getTotalDuration() >= requiredScore;
			default -> false;
		};
	}

	public double calculateProgress(TodayRecords todayRecords) {
		return switch (this) {
			case DISTANCE -> (todayRecords.getTotalDistance() / requiredScore);
			case DURATION -> ((double)todayRecords.getTotalDuration() / requiredScore);
			default -> 0;
		};
	}
}
