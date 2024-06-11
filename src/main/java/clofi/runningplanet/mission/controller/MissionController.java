package clofi.runningplanet.mission.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.service.MissionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MissionController {

	private final MissionService missionService;

	@GetMapping("/api/crew/{crewId}/mission")
	public ResponseEntity<CrewMissionListDto> getMissionList(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal
		CustomOAuth2User principal) {
		return ResponseEntity.ok(missionService.getCrewMission(crewId, principal.getId()));
	}

	@PostMapping("api/crew/{crewId}/mission/{missionId}")
	public ResponseEntity<Void> successMission(@PathVariable("crewId") Long crewId,
		@PathVariable("missionId") Long missionId, @AuthenticationPrincipal CustomOAuth2User principal) {
		missionService.successMission(crewId, missionId, principal.getId());
		return ResponseEntity.ok().build();
	}
}
