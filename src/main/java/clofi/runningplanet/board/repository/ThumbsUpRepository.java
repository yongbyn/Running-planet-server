package clofi.runningplanet.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.board.domain.Board;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUpRepository, Long> {
	List<ThumbsUpRepository> findAllByBoard(Board board);
}
