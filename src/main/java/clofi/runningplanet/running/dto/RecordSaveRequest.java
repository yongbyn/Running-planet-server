package clofi.runningplanet.running.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecordSaveRequest(
	double latitude,

	double longitude,

	@Min(0)
	int runTime,

	@DecimalMin("0")
	double runDistance,

	@Min(0)
	int calories,

	@NotNull
	AvgPace avgPace,

	boolean isEnd
) {
	public record AvgPace(
		@Min(0)
		int min,

		@Min(0)
		int sec
	) {
	}
}
