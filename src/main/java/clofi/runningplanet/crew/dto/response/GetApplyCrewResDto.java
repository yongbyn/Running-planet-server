package clofi.runningplanet.crew.dto.response;

import clofi.runningplanet.crew.domain.Approval;
import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.member.domain.Gender;

public record GetApplyCrewResDto(
	Long memberId,
	String nickname,
	String introduction,
	int runScore,
	Gender gender,
	int age,
	Approval approveStatus
) {
	public GetApplyCrewResDto(CrewApplication crewApplication) {
		this(
			crewApplication.getMember().getId(),
			crewApplication.getMember().getNickname(),
			crewApplication.getIntroduction(),
			crewApplication.getMember().getRunScore(),
			crewApplication.getMember().getGender(),
			crewApplication.getMember().getAge(),
			crewApplication.getApproval()
		);
	}
}
