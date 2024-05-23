package clofi.runningplanet.member.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update social_login set deleted_at = now() where social_login_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SocialLogin extends BaseSoftDeleteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "social_login_id", nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "o_auth_id", nullable = false, length = 50)
	private String oauthId;

	@Enumerated(EnumType.STRING)
	@Column(name = "o_auth_type", nullable = false, length = 10)
	private OAuthType oauthType;

	@Column(name = "external_email", nullable = false, length = 50)
	private String externalEmail;

	@Builder
	public SocialLogin(Long id, Member member, String oauthId, OAuthType oauthType, String externalEmail) {
		this.id = id;
		this.member = member;
		this.oauthId = oauthId;
		this.oauthType = oauthType;
		this.externalEmail = externalEmail;
	}
}
