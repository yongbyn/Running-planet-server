package clofi.runningplanet.running.dto;

public record AvgPaceResponse(
	int min,
	int sec
) {
	public AvgPaceResponse(int avgPace) {
		this(avgPace / 60, avgPace % 60);
	}
}
