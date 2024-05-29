package clofi.runningplanet.board.core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.core.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.core.dto.request.UpdateBoardRequest;
import clofi.runningplanet.board.core.dto.response.BoardDetailResponse;
import clofi.runningplanet.board.core.dto.response.BoardResponse;
import clofi.runningplanet.board.core.dto.response.CreateBoardResponse;
import clofi.runningplanet.board.core.service.BoardQueryService;
import clofi.runningplanet.board.core.service.BoardReadService;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {
	private final BoardQueryService boardQueryService;
	private final BoardReadService boardReadService;

	@PostMapping(value = "/api/crew/{crewId}/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	private ResponseEntity<CreateBoardResponse> create(
		@PathVariable(value = "crewId") Long crewId,
		@RequestPart(value = "createBoard") @Valid CreateBoardRequest createBoardRequest,
		@RequestPart(value = "imgFile", required = false) List<MultipartFile> imageFile,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(boardQueryService.create(crewId, createBoardRequest, imageFile, memberId));
	}

	@GetMapping("/api/crew/{crewId}/board")
	private ResponseEntity<List<BoardResponse>> getBoardList(
		@PathVariable(value = "crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();
		return ResponseEntity.ok(boardReadService.getBoardList(crewId, memberId));
	}

	@GetMapping("/api/crew/{crewId}/board/{boardId}")
	private ResponseEntity<BoardDetailResponse> getBoardDetail(
		@PathVariable(value = "crewId") Long crewId,
		@PathVariable(value = "boardId") Long boardId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();

		return ResponseEntity.ok(boardReadService.getBoardDetail(crewId, boardId, memberId));
	}

	@PatchMapping(value = "/api/crew/{crewId}/board/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	private ResponseEntity<CreateBoardResponse> updateBoard(
		@PathVariable(value = "crewId") Long crewId,
		@PathVariable(value = "boardId") Long boardId,
		@RequestPart(value = "updateRequest") @Valid UpdateBoardRequest updateBoardRequest,
		@RequestPart(value = "imgFile", required = false) List<MultipartFile> imageFile,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();
		return ResponseEntity.status(HttpStatus.OK)
			.body(boardQueryService.update(crewId, boardId, updateBoardRequest, imageFile, memberId));
	}

	@DeleteMapping("/api/crew/{crewId}/board/{boardId}")
	private ResponseEntity<Void> deleteBoard(
		@PathVariable("crewId") Long crewId,
		@PathVariable("boardId") Long boardId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User

	) {
		Long memberId = customOAuth2User.getId();
		boardQueryService.deleteBoard(crewId, boardId, memberId);
		return ResponseEntity.noContent().build();
	}
}
