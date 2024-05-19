package clofi.runningplanet.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.board.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
