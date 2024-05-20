package clofi.runningplanet.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBoardRequest {
	private String title;
	private String content;

	public CreateBoardRequest(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
