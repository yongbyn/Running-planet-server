package clofi.runningplanet.planet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.planet.dto.request.UpdatePlanetNameRequest;
import clofi.runningplanet.planet.dto.response.PlanetResponse;
import clofi.runningplanet.planet.service.PlanetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlanetController {

	private final PlanetService planetService;

	@GetMapping("/api/profile/{memberId}/planet")
	public ResponseEntity<List<PlanetResponse>> getPlanetList(
		@PathVariable(value = "memberId") Long memberId
	) {
		return ResponseEntity.ok(planetService.getPlanetList(memberId));
	}

	@PatchMapping("/api/profile/planet/{planetId}")
	public ResponseEntity<Long> updatePlanet(
		@PathVariable(value = "planetId") Long planetId,
		@RequestBody @Valid UpdatePlanetNameRequest updatePlanetNameRequest,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long ownerId = customOAuth2User.getId();
		return ResponseEntity.status(HttpStatus.OK)
			.body(planetService.updatePlanet(planetId, updatePlanetNameRequest, ownerId));
	}
}
