package clofi.runningplanet.crew.dto.request;

import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.member.domain.Member;

public record ApplyCrewReqDto(
	String introduction
) {
	public CrewApplication toEntity(Crew crew, Member member) {
		return new CrewApplication(introduction, crew, member);
	}
}
