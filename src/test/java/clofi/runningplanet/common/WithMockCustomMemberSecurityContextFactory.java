package clofi.runningplanet.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.dto.CustomOAuth2User;

public class WithMockCustomMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomMember> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomMember customMember) {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		CustomOAuth2User principal = new CustomOAuth2User(Member.builder().id(Long.valueOf(customMember.id())).build());
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password",
			principal.getAuthorities());
		securityContext.setAuthentication(auth);
		return securityContext;
	}
}
