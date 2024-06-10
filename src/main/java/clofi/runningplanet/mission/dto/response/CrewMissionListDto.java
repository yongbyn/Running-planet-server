package clofi.runningplanet.mission.dto.response;

import java.util.List;

public record CrewMissionListDto(
	List<GetCrewMissionResDto> missions
) {
}
