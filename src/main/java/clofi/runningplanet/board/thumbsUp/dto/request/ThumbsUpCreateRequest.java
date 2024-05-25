package clofi.runningplanet.board.thumbsUp.dto.request;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.ThumbsUp;
import clofi.runningplanet.member.domain.Member;

public record ThumbsUpCreateRequest(
	Board board,
	Member member
) {
	public static ThumbsUp toThumbsUp(Board board, Member member) {
		return new ThumbsUp(
			board,
			member
		);
	}

}
