package clofi.runningplanet.board.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.board.comment.dto.request.CreateCommentRequest;
import clofi.runningplanet.board.comment.dto.request.UpdateCommentRequest;
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

	@DeleteMapping("/api/crew/{crewId}/board/{boardId}/comment/{commentId}")
	private ResponseEntity<Void> deleteComment(
		@PathVariable("crewId") Long crewId,
		@PathVariable("boardId") Long boardId,
		@PathVariable("commentId") Long commentId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();
		commentService.deleteComment(crewId, boardId, commentId, memberId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/api/crew/{crewId}/board/{boardId}/comment/{commentId}")
	private ResponseEntity<Long> updateComment(
		@PathVariable("crewId") Long crewId,
		@PathVariable("boardId") Long boardId,
		@PathVariable("commentId") Long commentId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody UpdateCommentRequest updateCommentRequest
	) {
		Long memberId = customOAuth2User.getId();
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentService.updateComment(crewId, boardId, commentId, memberId, updateCommentRequest));
	}
}
