package clofi.runningplanet.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.dto.response.BoardResponse;
import clofi.runningplanet.board.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {
	private final BoardService boardService;

	@PostMapping(value = "/api/crew/{crewId}/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	private ResponseEntity<BoardResponse> create(
		@PathVariable(value = "crewId") Long crewId,
		@RequestPart(value = "createBoard") @Valid CreateBoardRequest createBoardRequest,
		@RequestPart(value = "imageFile") List<MultipartFile> imageFile
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(boardService.create(crewId, createBoardRequest, imageFile));
	}
}
