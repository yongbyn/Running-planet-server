package clofi.runningplanet.security.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import clofi.runningplanet.member.service.MemberService;
import clofi.runningplanet.security.jwt.JWTFilter;
import clofi.runningplanet.security.jwt.JWTUtil;
import clofi.runningplanet.security.oauth2.CustomSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final MemberService memberService;
	private final CustomSuccessHandler customSuccessHandler;
	private final JWTUtil jwtUtil;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable);

		http
			.formLogin(AbstractHttpConfigurer::disable);

		http
			.httpBasic(AbstractHttpConfigurer::disable);

		// http
		// 	.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		//토큰 만료시 무한 루프 오류 해결
		http
			.addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(memberService))
				.successHandler(customSuccessHandler)
			);

		//경로별 인가 작업
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/error", "/ws/**", "/h2-console/**","/api/kakaologin").permitAll()
				.anyRequest().authenticated());

		http
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

		http
			.exceptionHandling(e -> e.authenticationEntryPoint(new AuthenticationEntryPoint()));

		//세션 설정 : STATELESS
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		//CORS
		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "https://runple.site"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);

				configuration.setExposedHeaders(Collections.singletonList("Authorization"));

				return configuration;
			}));

		return http.build();
	}

	public static class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
		public AuthenticationEntryPoint() {
			super("");
		}

		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException)
			throws IOException {
			response.sendError(401, "Unauthorized");
		}
	}
}
