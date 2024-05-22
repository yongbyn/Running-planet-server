package clofi.runningplanet.crew.dto.response;

import java.util.List;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.RuleDto;

public record FindCrewResDto(
	Long crewId,
	int crewLevel,
	String crewName,
	CrewLeaderDto crewLeader,
	int memberCnt,
	int limitMemberCnt,
	ApprovalType approvalType,
	int limitRunScore,
	String introduction,
	List<String> tags,
	Category category,
	RuleDto rule,
	int crewTotalDistance,
	boolean isRequest
) {
	public static FindCrewResDto of(Crew crew, CrewLeaderDto crewLeader, List<String> tags) {
		return new FindCrewResDto(
			crew.getId(),
			1,
			crew.getCrewName(),
			crewLeader,
			1,
			crew.getLimitMemberCnt(),
			crew.getApprovalType(),
			crew.getLimitRunScore(),
			crew.getIntroduction(),
			tags,
			crew.getCategory(),
			new RuleDto(crew.getRuleRunCnt(), crew.getRuleDistance()),
			crew.getTotalDistance(),
			false
		);
	}
}
