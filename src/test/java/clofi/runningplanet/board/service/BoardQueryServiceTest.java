package clofi.runningplanet.board.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.BoardImage;
import clofi.runningplanet.board.dto.request.CreateBoardRequest;
import clofi.runningplanet.board.dto.response.CreateBoardResponse;
import clofi.runningplanet.board.factory.BoardFactory;
import clofi.runningplanet.board.factory.fake.FakeS3StorageManager;
import clofi.runningplanet.board.repository.BoardImageRepository;
import clofi.runningplanet.board.repository.BoardRepository;
import clofi.runningplanet.crew.repository.CrewRepository;

@SpringBootTest
class BoardQueryServiceTest {

	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private BoardImageRepository boardImageRepository;
	@Autowired
	private CrewRepository crewRepository;

	@AfterEach
	void tearDown() {
		boardImageRepository.deleteAllInBatch();
		boardRepository.deleteAllInBatch();
	}

	@DisplayName("사용자는 게시글을 작성할 수 있다.")
	@Test
	void createBoardTest(){
    	//given
		Long crewId = 1L;
		CreateBoardRequest createBoardRequest = new CreateBoardRequest("게시판 제목", "게시판 내용");
		List<MultipartFile> imageFile = getImageFile();
		BoardQueryService boardQueryService = getBoardService();

		//when
		CreateBoardResponse createBoardResponse = boardQueryService.create(crewId, createBoardRequest, imageFile);
		Board board = boardRepository.findById(createBoardResponse.boardId())
			.orElseThrow(() ->new IllegalArgumentException("게시판이 없습니다"));
		List<BoardImage> boardImage = boardImageRepository.findAllByBoard(board);
		//then
		assertThat(board.getId()).isEqualTo(createBoardResponse.boardId());
		assertThat(board.getTitle()).isEqualTo("게시판 제목");
		assertThat(board.getContent()).isEqualTo("게시판 내용");
		assertThat(boardImage.stream().map(BoardImage::getImageUrl).collect(Collectors.toList()))
			.containsExactlyInAnyOrder("fakeImageUrl1", "fakeImageUrl2");
	}

	private List<MultipartFile> getImageFile() {

		return Arrays.asList(
			new MockMultipartFile(
				"image1", // 파일 파라미터 이름
				"image1.jpg", // 파일명
				"image/jpeg", // 컨텐츠 타입
				"이미지_콘텐츠1".getBytes() // 파일 콘텐츠
			),
			new MockMultipartFile(
				"image2", // 파일 파라미터 이름
				"image2.jpg", // 파일명
				"image/jpeg", // 컨텐츠 타입
				"이미지_콘텐츠2".getBytes() // 파일 콘텐츠
			)
		);
	}

	private BoardQueryService getBoardService() {
		FakeS3StorageManager fakeS3StorageManager = new FakeS3StorageManager();
		return new BoardQueryService(
			new BoardFactory(
				boardRepository,
				boardImageRepository
			),
			crewRepository,
			fakeS3StorageManager
		);
	}
}