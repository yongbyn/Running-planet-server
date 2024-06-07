package clofi.runningplanet.crew.dto.request;

import java.util.List;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.dto.RuleDto;

public record UpdateCrewReqDto(
	List<String> tags,
	ApprovalType approvalType,
	String introduction,
	RuleDto rule
) {
}
