package clofi.runningplanet.board.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.core.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.core.dto.request.UpdateBoardRequest;
import clofi.runningplanet.board.core.dto.response.CreateBoardResponse;
import clofi.runningplanet.board.core.factory.BoardFactory;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardQueryService {

	private final BoardFactory boardFactory;
	private final CrewRepository crewRepository;
	private final BoardRepository boardRepository;
	private final S3StorageManagerUseCase storageManagerUseCase;

	public CreateBoardResponse create(Long crewId, CreateBoardRequest createBoardRequest,
		List<MultipartFile> imageFile) {

		//TODO: 멤버
		Crew crew = crewRepository.findById(crewId).orElseThrow(
			() -> new IllegalArgumentException("크루가 존재하지 않습니다"));

		List<String> imageUrlList = Optional.ofNullable(imageFile)
			.filter(image -> !image.isEmpty())
			.map(storageManagerUseCase::uploadImages)
			.orElseGet(ArrayList::new);

		return boardFactory.insert(crew, createBoardRequest, imageUrlList);
	}

	public CreateBoardResponse update(Long crewId, Long boardId, UpdateBoardRequest updateBoardRequest,
		List<MultipartFile> imageFile) {
		Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다."));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		boardFactory.update(crew, board, updateBoardRequest, imageFile);
		return CreateBoardResponse.of(board);
	}
}
