package clofi.runningplanet.board.core.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.core.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.core.dto.request.UpdateBoardRequest;
import clofi.runningplanet.board.core.dto.response.CreateBoardResponse;
import clofi.runningplanet.board.core.repository.BoardImageRepository;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.common.service.S3StorageManagerUseCase;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.BoardImage;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.member.domain.Member;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BoardFactory {
	private final BoardRepository boardRepository;
	private final BoardImageRepository boardImageRepository;
	private final S3StorageManagerUseCase s3StorageManagerUseCase;

	public CreateBoardResponse insert(Crew crew, CreateBoardRequest createBoardRequest, List<String> imageUrlList,
		Member member) {
		Board board = boardRepository.save(createBoardRequest.toBoard(crew, member));
		insertImage(board, imageUrlList);
		return CreateBoardResponse.of(board);
	}

	public void update(Crew crew, Board board, UpdateBoardRequest updateBoardRequest, List<MultipartFile> imageFile) {
		board.updateBoard(updateBoardRequest.getTitle(), updateBoardRequest.getContent());
		List<BoardImage> imgList = boardImageRepository.findAllByBoard(board);
		for (BoardImage boardImage : imgList) {
			s3StorageManagerUseCase.deleteImages(boardImage.getImageUrl());
		}
		boardImageRepository.deleteAllByBoard(board);
		List<String> imageUrl = s3StorageManagerUseCase.uploadImages(imageFile);
		insertImage(board, imageUrl);
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
