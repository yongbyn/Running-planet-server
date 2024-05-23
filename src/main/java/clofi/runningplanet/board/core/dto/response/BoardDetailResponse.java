package clofi.runningplanet.board.core.dto.response;

import java.util.List;

import clofi.runningplanet.board.comment.dto.response.CommentResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardDetailResponse {
	private BoardResponse boardResponse;
	private List<CommentResponse> comments;

	public BoardDetailResponse(BoardResponse boardResponses, List<CommentResponse> commentResponseList) {
		this.boardResponse = boardResponses;
		this.comments = commentResponseList;
	}
}
