package clofi.runningplanet.board.thumbsUp.service;

import org.springframework.stereotype.Service;

import clofi.runningplanet.board.core.repository.BoardRepository;
import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.ThumbsUp;
import clofi.runningplanet.board.thumbsUp.repository.ThumbsUpRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ThumbsUpService {

	private final ThumbsUpRepository thumbsUpRepository;
	private final MemberRepository memberRepository;
	private final BoardRepository boardRepository;

	public Long createLike(Long crewId, Long boardId, Long userId) {
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 게시글이 없습니다."));

		Boolean isExists = thumbsUpRepository.existsByMemberAndBoard(member, board);
		if (isExists) {
			thumbsUpRepository.delete(thumbsUpRepository.findByMemberAndBoard(member, board));
		} else {
			return thumbsUpRepository.save(new ThumbsUp(board, member)).getId();
		}
		return board.getId();
	}
}
