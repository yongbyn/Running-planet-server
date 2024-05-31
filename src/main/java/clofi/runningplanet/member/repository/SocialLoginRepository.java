package clofi.runningplanet.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import clofi.runningplanet.member.domain.OAuthType;
import clofi.runningplanet.member.domain.SocialLogin;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

	Boolean existsByOauthTypeAndOauthId(OAuthType oauthType, String oAuthId);

	@Query("SELECT sl FROM SocialLogin sl JOIN FETCH sl.member WHERE sl.oauthType = :oauthType AND sl.oauthId = :oauthId")
	SocialLogin findByOauthTypeAndOauthIdWithMember(@Param("oauthType") OAuthType oauthType,@Param("oauthId") String oauthId);

}
