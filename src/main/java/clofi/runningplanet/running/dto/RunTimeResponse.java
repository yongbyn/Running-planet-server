package clofi.runningplanet.running.dto;

public record RunTimeResponse(
	int hour,
	int min,
	int sec
) {
	public RunTimeResponse(int runTime) {
		this(runTime / (60 * 60), (runTime / 60) % (60 * 60), runTime % 60);
	}
}
