package clofi.runningplanet.board.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.board.comment.dto.request.CreateCommentRequest;
import clofi.runningplanet.board.comment.dto.request.UpdateCommentRequest;
import clofi.runningplanet.board.comment.repository.CommentRepository;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.Comment;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
	private final CommentRepository commentRepository;
	private final CrewRepository crewRepository;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	public Long create(Long crewId, Long boardId, CreateCommentRequest createCommentRequest, Long memberId) {
		crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다."));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
		Comment comment = commentRepository.save(createCommentRequest.toComment(board, member));
		return comment.getId();
	}

	public void deleteComment(Long crewId, Long boardId, Long commentId, Long memberId) {
		boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
		if (comment.getMember().getId() != memberId) {
			throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다.");
		}
		commentRepository.deleteById(commentId);
	}

	public Long updateComment(Long crewId, Long boardId, Long commentId, Long memberId,
		UpdateCommentRequest updateCommentRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
		crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("크루가 없습니다."));
		boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("작성된 게시글이 없습니다."));
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("작성된 댓글이 없습니다."));

		if (comment.getMember().getId() != member.getId()) {
			throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다.");
		}
		comment.updateComment(updateCommentRequest);
		return comment.getId();
	}
}
