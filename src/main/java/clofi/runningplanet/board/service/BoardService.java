package clofi.runningplanet.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.dto.CreateBoardRequest;
import clofi.runningplanet.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {
	private final BoardRepository boardRepository;
	public Long create(Long crewId, CreateBoardRequest createBoardRequest, MultipartFile imageFile) {
		return null;
	}
}
