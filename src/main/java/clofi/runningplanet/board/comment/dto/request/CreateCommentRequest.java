package clofi.runningplanet.board.comment.dto.request;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.board.domain.Comment;
import clofi.runningplanet.member.domain.Member;

public record CreateCommentRequest(
	String content
) {
	public Comment toComment(Board board, Member member) {
		return new Comment(board, content, member);
	}
}
