package clofi.runningplanet.crew.dto.request;

import java.util.List;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.RuleDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCrewReqDto(
	@NotEmpty
	@Size(max = 50)
	String crewName,

	@NotNull
	Category category,

	List<String> tags,

	@NotNull
	ApprovalType approvalType,

	@Size(max = 4000)
	String introduction,

	@NotNull
	RuleDto rule
) {
	public Crew toEntity(Long leaderId) {
		return new Crew(leaderId, crewName, 10, category, approvalType, introduction,
			rule.weeklyRun(), rule.distance());
	}
}
