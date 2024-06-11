package clofi.runningplanet.planet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.planet.dto.response.PlanetResponse;
import clofi.runningplanet.planet.service.PlanetService;
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
}
