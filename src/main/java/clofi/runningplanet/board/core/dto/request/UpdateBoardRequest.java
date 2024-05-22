package clofi.runningplanet.board.core.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBoardRequest {
	@NotEmpty(message = "제목은 공백일 수 없습니다.")
	private String title;
	@NotEmpty(message = "내용은 공백일 수 없습니다.")
	private String content;

	public UpdateBoardRequest(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
