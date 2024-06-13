package clofi.runningplanet.socket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import clofi.runningplanet.common.exception.UnauthorizedException;
import clofi.runningplanet.member.domain.CustomUser;
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

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String token = jwtUtil.extractToken(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER));
			if (token == null || jwtUtil.isExpired(token)) {
				throw new UnauthorizedException("Invalid token");
			}
			Long userId = jwtUtil.getUserId(token);
			Authentication authentication = createAuthentication(userId);
			accessor.setUser(authentication);
		}

		String logMessage = accessor.getShortLogMessage(message.getPayload());
		log.info("Socket Log Message={}", logMessage);
		return message;
	}

	private Authentication createAuthentication(Long userId) {
		CustomUser user = new CustomUser(userId);
		return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	}
}
