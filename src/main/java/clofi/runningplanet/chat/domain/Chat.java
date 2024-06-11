package clofi.runningplanet.chat.domain;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update chat set deleted_at = now() where chat_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat extends BaseSoftDeleteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id", nullable = false)
	private Crew crew;

	@Column(name = "content", nullable = false, length = 255)
	private String content;

	@Builder
	public Chat(Long id, Member member, Crew crew, String content) {
		this.id = id;
		this.member = member;
		this.crew = crew;
		this.content = content;
	}
}
