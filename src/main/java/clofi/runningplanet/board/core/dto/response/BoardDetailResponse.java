package clofi.runningplanet.board.core.dto.response;

import java.util.List;

import clofi.runningplanet.board.comment.dto.response.CommentResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardDetailResponse {
	private BoardResponse boardResponse;
	private Long authorId;
	private Boolean isLiked;
	private List<CommentResponse> comments;

	public BoardDetailResponse(BoardResponse boardResponses, Long authorId, Boolean isThumbsUp,
		List<CommentResponse> commentResponseList) {
		this.boardResponse = boardResponses;
		this.authorId = authorId;
		this.isLiked = isThumbsUp;
		this.comments = commentResponseList;
	}
}
