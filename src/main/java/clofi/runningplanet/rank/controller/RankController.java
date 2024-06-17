package clofi.runningplanet.rank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.rank.dto.CrewRankResponse;
import clofi.runningplanet.rank.service.RankService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RankController {
	private final RankService rankService;

	@GetMapping("/api/ranking/crew")
	public ResponseEntity<List<CrewRankResponse>> crew(
		@RequestParam("condition") String condition,
		@RequestParam("period") String period
	) {
		return ResponseEntity.ok(rankService.getCrewRankList(condition, period));
	}
}
