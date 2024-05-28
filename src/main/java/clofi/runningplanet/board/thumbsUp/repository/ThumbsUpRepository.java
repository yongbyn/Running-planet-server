package clofi.runningplanet.board.thumbsUp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.ThumbsUp;
import clofi.runningplanet.member.domain.Member;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
	Boolean existsByMemberAndBoard(Member member, Board board);

	ThumbsUp findByMemberAndBoard(Member member, Board board);

	List<ThumbsUp> findAllByBoard(Board board);

	void deleteAllByBoard(Board board);
}
