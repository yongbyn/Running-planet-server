package clofi.runningplanet.board.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.BoardImage;
import clofi.runningplanet.board.dto.response.BoardResponse;
import clofi.runningplanet.board.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.repository.BoardImageRepository;
import clofi.runningplanet.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BoardFactory {
	private final BoardRepository boardRepository;
	private final BoardImageRepository boardImageRepository;

	public BoardResponse insert(Long crewId, CreateBoardRequest createBoardRequest, List<String> imageUrlList) {
		Board board = boardRepository.save(createBoardRequest.toBoard());
		insertImage(board, imageUrlList);
		return BoardResponse.of(board);
	}

	private void insertImage(Board board, List<String> imageUrlList) {
		if (imageUrlList != null && !imageUrlList.isEmpty()) {
			List<BoardImage> images = imageUrlList.stream()
				.map(imageUrl -> new BoardImage(board, imageUrl))
				.collect(Collectors.toList());
			boardImageRepository.saveAll(images);
		}
	}
}
