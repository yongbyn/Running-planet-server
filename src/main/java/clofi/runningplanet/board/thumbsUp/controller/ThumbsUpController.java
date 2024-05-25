package clofi.runningplanet.board.thumbsUp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.board.thumbsUp.service.ThumbsUpService;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ThumbsUpController {

	private final ThumbsUpService thumbsUpService;

	@PostMapping("/api/crew/{crewId}/board/{boardId}/like")
	public ResponseEntity<Long> like(
		@PathVariable("crewId") Long crewId,
		@PathVariable("boardId") Long boardId,
		@AuthenticationPrincipal CustomOAuth2User user) {
		Long userId = user.getId();
		return ResponseEntity.status(HttpStatus.CREATED).body(thumbsUpService.createLike(crewId, boardId, userId));
	}

}
