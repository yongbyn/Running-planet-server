package clofi.runningplanet.board.dto.response;

import java.util.Stack;

import clofi.runningplanet.board.domain.Board;
import lombok.Getter;

public record BoardResponse(Long boardId) {

	public static BoardResponse of(Board board) {
		return new BoardResponse(
			board.getId()
		);
	}
}
