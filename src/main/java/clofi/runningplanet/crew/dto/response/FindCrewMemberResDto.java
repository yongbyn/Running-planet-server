package clofi.runningplanet.crew.dto.response;

import clofi.runningplanet.crew.domain.CrewMember;

public record FindCrewMemberResDto(
	Long memberId,
	String nickname,
	int missionCnt,
	boolean crewLeader
) {
	public FindCrewMemberResDto(CrewMember crewMember, int missionCnt) {
		this(crewMember.getMember().getId(), crewMember.getMember().getNickname(), missionCnt, crewMember.isLeader());
	}
}
