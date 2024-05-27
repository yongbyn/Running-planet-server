package clofi.runningplanet.board.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.board.comment.dto.request.CreateCommentRequest;
import clofi.runningplanet.board.comment.service.CommentService;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/api/crew/{crewId}/board/{boardId}/comment")
	private ResponseEntity<Long> create(
		@PathVariable(value = "crewId") Long crewId,
		@PathVariable(value = "boardId") Long boardId,
		@RequestBody CreateCommentRequest createCommentRequest,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentService.create(crewId, boardId, createCommentRequest, memberId));
	}
}
