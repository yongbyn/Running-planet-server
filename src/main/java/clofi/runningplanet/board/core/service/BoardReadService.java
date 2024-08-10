package clofi.runningplanet.board.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.board.comment.dto.response.CommentResponse;
import clofi.runningplanet.board.comment.repository.CommentRepository;
import clofi.runningplanet.board.core.dto.response.BoardDetailResponse;
import clofi.runningplanet.board.core.dto.response.BoardResponse;
import clofi.runningplanet.board.core.dto.response.ImageList;
import clofi.runningplanet.board.core.repository.BoardImageRepository;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.Comment;
import clofi.runningplanet.board.domain.ThumbsUp;
import clofi.runningplanet.board.thumbsUp.repository.ThumbsUpRepository;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
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
	private final MemberRepository memberRepository;

	public List<BoardResponse> getBoardList(Long crewId, Long memberId) {
		List<BoardResponse> boardResponses = new ArrayList<>();
		memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다"));

		List<Board> boardList = boardRepository.findAllByCrew(crew);
		boardList.forEach(board -> {
			List<ImageList> boardImageList = boardImageRepository.findAllByBoard(board)
				.stream().map(ImageList::of).collect(Collectors.toList());
			List<Comment> commentList = commentRepository.findAllByBoard(board);
			List<ThumbsUp> thumbsUpList = thumbsUpRepository.findAllByBoard(board);
			boardResponses.add(new BoardResponse(board, boardImageList, commentList.size(), thumbsUpList.size()));
		});
		return boardResponses;
	}

	public BoardDetailResponse getBoardDetail(Long crewId, Long boardId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
		crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다."));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		List<ImageList> boardImageList = boardImageRepository.findAllByBoard(board)
			.stream().map(ImageList::of).toList();

		List<CommentResponse> commentResponseList = new ArrayList<>();

		List<Comment> commentList = commentRepository.findAllByBoard(board);
		commentList.forEach(comment -> {
			boolean isModified = !comment.getCreatedAt().equals(comment.getUpdatedAt());
			commentResponseList.add(new CommentResponse(comment, isModified));
		});
		List<ThumbsUp> thumbsUpList = thumbsUpRepository.findAllByBoard(board);
		Boolean isThumbsUp = thumbsUpRepository.existsByMemberAndBoard(member, board);
		BoardResponse boardResponse = new BoardResponse(board, boardImageList, commentList.size(), thumbsUpList.size());

		return new BoardDetailResponse(boardResponse, board.getMember().getId(), isThumbsUp, commentResponseList);
	}
}
