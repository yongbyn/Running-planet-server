package clofi.runningplanet.socket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
	private static final String AUTHORIZATION_HEADER = "Authorization";

	private final JWTUtil jwtUtil;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT == accessor.getCommand()) {
			String token = extractToken(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER));
			Long userId = jwtUtil.getUserId(token);
			Authentication authentication = createAuthentication(userId);
			accessor.setUser(authentication);
		}

		String logMessage = accessor.getShortLogMessage(message.getPayload());
		log.info("Socket Log Message={}", logMessage);
		return message;
	}

	public String extractToken(String bearerToken) {
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private Authentication createAuthentication(Long userId) {
		Member member = Member.builder()
			.id(userId)
			.build();
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(member);
		return new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
	}
}
