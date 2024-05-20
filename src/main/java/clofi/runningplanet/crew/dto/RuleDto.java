package clofi.runningplanet.crew.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;

public record RuleDto(
	@Range(min = 1, max = 7)
	int weeklyRun,

	@Min(1)
	int distance
) {
}
