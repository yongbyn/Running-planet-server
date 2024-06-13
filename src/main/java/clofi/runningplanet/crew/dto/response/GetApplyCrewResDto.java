package clofi.runningplanet.crew.dto.response;

import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.member.domain.Gender;

public record GetApplyCrewResDto(
	Long memberId,
	String nickname,
	String introduction,
	Gender gender,
	int age,
	String userImg
) {
	public GetApplyCrewResDto(CrewApplication crewApplication) {
		this(
			crewApplication.getMember().getId(),
			crewApplication.getMember().getNickname(),
			crewApplication.getIntroduction(),
			crewApplication.getMember().getGender(),
			crewApplication.getMember().getAge(),
			crewApplication.getMember().getProfileImg()
		);
	}
}
