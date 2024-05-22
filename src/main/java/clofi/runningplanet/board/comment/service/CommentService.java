package clofi.runningplanet.board.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.board.comment.dto.request.CreateCommentRequest;
import clofi.runningplanet.board.comment.repository.CommentRepository;
import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.Comment;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.security.oauth2.CustomSuccessHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
	private final CommentRepository commentRepository;
	private final CrewRepository crewRepository;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	public Long create(Long crewId, Long boardId, CreateCommentRequest createCommentRequest, String customOAuth2User) {
		crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("크루가 존재하지 않습니다."));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		Member member = memberRepository.findByNickname(customOAuth2User);
		Comment comment = commentRepository.save(createCommentRequest.toComment(board, member));
		return comment.getId();
	}
}
