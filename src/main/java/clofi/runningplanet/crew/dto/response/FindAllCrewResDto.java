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
	String imgFile,
	List<String> tags,
	Category category,
	RuleDto rule,
	String introduction,
	CrewLeaderDto crewLeader
) {
	public static FindAllCrewResDto of(Crew crew, int memberCnt, List<String> tags, CrewLeaderDto crewLeader,
		String filePath) {
		return new FindAllCrewResDto(
			crew.getId(),
			crew.getCrewName(),
			crew.getCrewLevel(),
			memberCnt,
			crew.getLimitMemberCnt(),
			crew.getApprovalType(),
			filePath,
			tags,
			crew.getCategory(),
			new RuleDto(crew.getRuleRunCnt(), crew.getRuleDistance()),
			crew.getIntroduction(),
			crewLeader
		);
	}
}
