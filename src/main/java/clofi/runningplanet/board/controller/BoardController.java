package clofi.runningplanet.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.dto.request.UpdateBoardRequest;
import clofi.runningplanet.board.dto.response.BoardResponse;
import clofi.runningplanet.board.dto.response.CreateBoardResponse;
import clofi.runningplanet.board.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.service.BoardReadService;
import clofi.runningplanet.board.service.BoardQueryService;
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
		@RequestPart(value = "imageFile") List<MultipartFile> imageFile
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(boardQueryService.create(crewId, createBoardRequest, imageFile));
	}

	@GetMapping("/api/crew/{crewId}/board")
	private ResponseEntity<List<BoardResponse>> getBoardList(
		@PathVariable(value = "crewId") Long crewId
	) {
		return ResponseEntity.ok(boardReadService.getBoardList(crewId));
	}

	@PatchMapping("/api/crew/{crewId}/board/{boardId}")
	private ResponseEntity<CreateBoardResponse> updateBoard(
		@PathVariable(value = "crewId") Long crewId,
		@PathVariable(value = "boardId") Long boardId,
		@RequestPart(value = "createBoard") @Valid UpdateBoardRequest updateBoardRequest,
		@RequestPart(value = "imageFile") List<MultipartFile> imageFile
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(boardQueryService.update(crewId, boardId, updateBoardRequest, imageFile));
	}
}
