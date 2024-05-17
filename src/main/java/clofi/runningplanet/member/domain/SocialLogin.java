package clofi.runningplanet.member.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@SQLDelete(sql = "update social_login set is_delete = true where social_login = ?")
@SQLRestriction("is_delete = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SocialLogin extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "social_login_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "o_auth_id", nullable = false, length = 50)
	private String oAuthId;

	@Enumerated(EnumType.STRING)
	@Column(name = "o_auth_type", nullable = false, length = 10)
	private OAuthType oAuthType;

	@Column(name = "external_email", nullable = false, length = 50)
	private String externalEmail;
}
