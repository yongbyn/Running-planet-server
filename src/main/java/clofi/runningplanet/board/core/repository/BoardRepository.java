package clofi.runningplanet.board.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.crew.domain.Crew;

public interface BoardRepository extends JpaRepository<Board, Long> {
	List<Board> findAllByCrew(Crew crew);
}
