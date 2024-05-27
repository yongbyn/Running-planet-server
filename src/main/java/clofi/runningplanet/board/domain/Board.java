package clofi.runningplanet.board.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update board set deleted_at = now() where board_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseSoftDeleteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id", nullable = false)
	private Crew crew;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	public Board(String title, String content, Crew crew, Member member) {
		this.title = title;
		this.content = content;
		this.crew = crew;
		this.member = member;
	}

	public void updateBoard(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
