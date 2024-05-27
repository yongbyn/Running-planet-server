package clofi.runningplanet.board.comment.dto.response;

import java.time.LocalDateTime;

import clofi.runningplanet.board.domain.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {
	private Long id;
	private String author;
	private String content;
	private LocalDateTime createdDate;
	private Boolean isModified;
	private String authorImg;

	public CommentResponse(Comment comment, Boolean isModified) {
		this.id = comment.getId();
		this.author = comment.getMember().getNickname();
		this.content = comment.getContent();
		this.createdDate = comment.getCreatedAt();
		this.isModified = isModified;
		this.authorImg = comment.getMember().getProfileImg();
	}
}
