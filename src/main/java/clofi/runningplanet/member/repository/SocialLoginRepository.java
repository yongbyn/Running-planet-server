package clofi.runningplanet.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.OAuthType;
import clofi.runningplanet.member.domain.SocialLogin;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

	Boolean existsByOauthTypeAndOauthId(OAuthType oauthType, String oAuthId);

	SocialLogin findByOauthTypeAndOauthId(OAuthType oauthType, String oAuthId);

}
