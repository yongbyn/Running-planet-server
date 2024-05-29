package clofi.runningplanet.member.dto.request;

import clofi.runningplanet.member.domain.Gender;

public record CreateOnboardingRequest(
	Gender gender,
	int age,
	int weight
) {
}
