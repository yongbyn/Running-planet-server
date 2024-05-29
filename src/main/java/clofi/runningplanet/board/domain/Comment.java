package clofi.runningplanet.board.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.board.comment.dto.request.UpdateCommentRequest;
import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
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
@SQLDelete(sql = "update comment set deleted_at = now() where comment_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseSoftDeleteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "content", nullable = false, length = 255)
	private String content;

	public Comment(Board board, String content, Member member) {
		this.board = board;
		this.content = content;
		this.member = member;
	}

	public void updateComment(UpdateCommentRequest updateCommentRequest) {
		this.content = updateCommentRequest.content();
	}
}
