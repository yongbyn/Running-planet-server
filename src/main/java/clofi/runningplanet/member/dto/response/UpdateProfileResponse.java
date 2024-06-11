package clofi.runningplanet.member.dto.response;

import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;

public record UpdateProfileResponse(
	String nickname,
	int weight,
	Gender gender,
	int age,
	String profileImage
) {
	public UpdateProfileResponse(Member member) {
		this(member.getNickname(), member.getWeight(), member.getGender(), member.getAge(), member.getProfileImg());
	}
}
