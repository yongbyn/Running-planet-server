package clofi.runningplanet.mission.dto.response;

import clofi.runningplanet.mission.domain.MissionType;

public record GetCrewMissionResDto(
	Long missionId,
	MissionType missionContent,
	double missionProgress,
	boolean missionComplete
) {
}
