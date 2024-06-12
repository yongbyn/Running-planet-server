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
	String introduction,
	String imgFile,
	List<String> tags,
	Category category,
	RuleDto rule,
	int crewTotalDistance,
	boolean isRequest
) {
	public static FindCrewResDto of(Crew crew, int memberCnt, CrewLeaderDto crewLeader, List<String> tags,
		String filePath) {
		return new FindCrewResDto(
			crew.getId(),
			crew.getCrewLevel(),
			crew.getCrewName(),
			crewLeader,
			memberCnt,
			crew.getLimitMemberCnt(),
			crew.getApprovalType(),
			crew.getIntroduction(),
			filePath,
			tags,
			crew.getCategory(),
			new RuleDto(crew.getRuleRunCnt(), crew.getRuleDistance()),
			crew.getTotalDistance(),
			false
		);
	}
}
