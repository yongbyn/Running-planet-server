package clofi.runningplanet.crew.dto.response;

import java.util.List;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.RuleDto;

public record FindAllCrewResDto(
	Long crewId,
	String crewName,
	int crewLevel,
	int memberCnt,
	int limitMemberCnt,
	ApprovalType approvalType,
	int limitRunScore,
	List<String> tags,
	Category category,
	RuleDto rule,
	String introduction,
	CrewLeaderDto crewLeader
) {
	public static FindAllCrewResDto of(Crew crew, List<String> tags, CrewLeaderDto crewLeader) {
		return new FindAllCrewResDto(
			crew.getId(),
			crew.getCrewName(),

			// todo member, mission 기능 구현 후 로직 개선
			1,
			1,

			crew.getLimitMemberCnt(),
			crew.getApprovalType(),
			crew.getLimitRunScore(),
			tags,
			crew.getCategory(),
			new RuleDto(crew.getRuleRunCnt(), crew.getRuleDistance()),
			crew.getIntroduction(),
			crewLeader
		);
	}
}
