package clofi.runningplanet.board.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.board.core.repository.BoardImageRepository;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.comment.repository.CommentRepository;
import clofi.runningplanet.board.core.repository.ThumbsUpRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.Comment;
import clofi.runningplanet.board.core.dto.response.BoardResponse;
import clofi.runningplanet.board.core.dto.response.ImageList;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardReadService {

	private final BoardRepository boardRepository;
	private final BoardImageRepository boardImageRepository;
	private final CrewRepository crewRepository;
	private final CommentRepository commentRepository;
	private final ThumbsUpRepository thumbsUpRepository;

	public List<BoardResponse> getBoardList(Long crewId) {
		List<BoardResponse> boardResponses = new ArrayList<>();
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다"));
		List<Board> boardList = boardRepository.findAllByCrew(crew);
		for (Board board : boardList) {
			List<ImageList> boardImageList = boardImageRepository.findAllByBoard(board)
				.stream().map(ImageList::of).toList();
			List<Comment> commentList = commentRepository.findAllByBoard(board);
			List<ThumbsUpRepository> thumbsUpList = thumbsUpRepository.findAllByBoard(board);
			boardResponses.add(new BoardResponse(board, boardImageList, commentList.size(), thumbsUpList.size()));
		}
		return boardResponses;
	}
}
