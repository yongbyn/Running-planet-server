package clofi.runningplanet.member.dto.request;

import clofi.runningplanet.member.domain.Gender;

public record UpdateProfileRequest(
	String nickname,
	int weight,
	Gender gender,
	int age
) {
}
