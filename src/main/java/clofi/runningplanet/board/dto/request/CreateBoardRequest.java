package clofi.runningplanet.board.dto.request;

import clofi.runningplanet.board.domain.Board;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBoardRequest {
	@NotEmpty(message = "제목은 공백일 수 없습니다.")
	private String title;
	@NotEmpty(message = "내용은 공백일 수 없습니다.")
	private String content;

	public CreateBoardRequest(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public Board toBoard() {
		return new Board(
			title,
			content
		);
	}
}
