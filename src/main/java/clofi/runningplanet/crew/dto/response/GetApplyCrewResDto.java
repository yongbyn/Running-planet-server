package clofi.runningplanet.crew.dto.response;

import clofi.runningplanet.crew.domain.Approval;
import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;

public record GetApplyCrewResDto(
	Long memberId,
	String nickname,
	String introduction,
	int runScore,
	Gender gender,
	int age,
	Approval approveStatus
) {
	public GetApplyCrewResDto(Member member, CrewApplication crewApplication) {
		this(member.getId(),
			member.getNickname(),
			crewApplication.getIntroduction(),
			member.getRunScore(),
			member.getGender(),
			member.getAge(),
			crewApplication.getApproval());
	}
}
