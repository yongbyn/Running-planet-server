package clofi.runningplanet.crew.dto.request;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;

public record RuleReqDto(
	@Range(min = 1, max = 7)
	int weeklyRun,

	@Min(1)
	int distance
) {
}
