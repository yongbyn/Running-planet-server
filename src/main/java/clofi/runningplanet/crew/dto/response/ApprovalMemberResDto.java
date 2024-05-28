package clofi.runningplanet.crew.dto.response;

import java.util.List;

public record ApprovalMemberResDto(
	List<GetApplyCrewResDto> approvalMember
) {
}
