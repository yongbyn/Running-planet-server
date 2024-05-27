package clofi.runningplanet.board.core.dto.response;

import java.time.LocalDate;
import java.util.List;

import clofi.runningplanet.board.domain.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardResponse {
	private Long id;
	private String title;
	private String author;
	private LocalDate writtenDate;
	private Integer commentCnt;
	private Integer likeCnt;
	private String content;
	private List<ImageList> imageList;

	public BoardResponse(Board board, List<ImageList> boardImageList, int commentCnt, int likeCnt) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.author = board.getMember().getNickname();
		this.writtenDate = board.getCreatedAt().toLocalDate();
		this.commentCnt = commentCnt;
		this.likeCnt = likeCnt;
		this.content = board.getContent();
		this.imageList = boardImageList;
	}
}
