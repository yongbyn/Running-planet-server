package clofi.runningplanet.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.dto.response.BoardResponse;
import clofi.runningplanet.board.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.factory.BoardFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {
	private final BoardFactory boardFactory;
	private final S3StorageManagerUseCase storageManagerUseCase;
	public BoardResponse create(Long crewId, CreateBoardRequest createBoardRequest, List<MultipartFile> imageFile) {

		//TODO: 멤버, 크루 validation 적용 필요

		List<String> imageUrlList = Optional.ofNullable(imageFile)
			.filter(image -> !image.isEmpty())
			.map(storageManagerUseCase::uploadImages)
			.orElseGet(ArrayList::new);

		return boardFactory.insert(crewId, createBoardRequest, imageUrlList);
	}
}
