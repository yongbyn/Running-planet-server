package clofi.runningplanet.board.core.dto.request;

import clofi.runningplanet.board.domain.Board;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.member.domain.Member;
import jakarta.validation.constraints.NotEmpty;
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

	public Board toBoard(Crew crew, Member member) {
		return new Board(
			title,
			content,
			crew,
			member
		);
	}
}
