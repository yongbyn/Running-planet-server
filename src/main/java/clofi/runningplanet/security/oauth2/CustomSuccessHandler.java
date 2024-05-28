package clofi.runningplanet.security.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.security.jwt.JWTUtil;
import clofi.runningplanet.security.jwt.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${login.redirect.url}")
	public String REDIRECT_URL;

	private final JWTUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

		Long userId = customUserDetails.getId();

		JwtToken jwt = jwtUtil.createJwt(userId, 60 * 60 * 60L * 1000);

		response.sendRedirect(REDIRECT_URL + "?Authorization=" + jwt.getAccessToken());
	}
}
