package clofi.runningplanet.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.ThumbsUp;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
	List<ThumbsUpRepository> findAllByBoard(Board board);
}
