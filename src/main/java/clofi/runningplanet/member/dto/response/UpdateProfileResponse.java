package clofi.runningplanet.member.dto.response;

import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.member.domain.Member;

public record UpdateProfileResponse(
	String nickname,
	String profileImage
) {
	public UpdateProfileResponse(Member member) {
		this(member.getNickname(), member.getProfileImg());
	}
}
