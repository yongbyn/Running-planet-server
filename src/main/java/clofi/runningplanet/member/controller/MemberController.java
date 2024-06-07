package clofi.runningplanet.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.member.dto.OauthToken;
import clofi.runningplanet.member.dto.request.CreateOnboardingRequest;
import clofi.runningplanet.member.dto.request.UpdateProfileRequest;
import clofi.runningplanet.member.dto.response.ProfileResponse;
import clofi.runningplanet.member.dto.response.SelfProfileResponse;
import clofi.runningplanet.member.dto.response.UpdateProfileResponse;
import clofi.runningplanet.member.service.MemberService;
import clofi.runningplanet.security.jwt.JWTUtil;
import clofi.runningplanet.security.jwt.JwtToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final JWTUtil jwtUtil;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String kakaoClientSecret;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUrl;

	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String kakaoGrantedType;

	@GetMapping("/api/kakaologin")
	public ResponseEntity<JwtToken> kakaoLogin(@RequestParam("code") String code) {

		RestTemplate restTemplate = new RestTemplate();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", kakaoGrantedType);
		params.add("client_id", kakaoClientId);
		params.add("redirect_uri", kakaoRedirectUrl);
		params.add("client_secret", kakaoClientSecret);
		params.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<String> authCodeResponse = null;
		try {
			authCodeResponse = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token",
				kakaoTokenRequest, String.class);
			log.info("kauth 성공 {}", authCodeResponse.getBody());
		} catch (RestClientException e) {
			log.error("kauth 실패 {}", e.getMessage());
			throw new RuntimeException(e);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		OauthToken oauthToken = null;

		try {
			oauthToken = objectMapper.readValue(authCodeResponse.getBody(), OauthToken.class);
			log.info("Kakao token: {}", oauthToken.getAccess_token());
		} catch (JsonProcessingException e) {
			log.error("Error parsing oauth token: {}", authCodeResponse);
			throw new RuntimeException(e);
		}

		RestTemplate restTemplate2 = new RestTemplate();
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

		ResponseEntity<String> kakaoProfileResponse = null;
		try {
			kakaoProfileResponse = restTemplate2.postForEntity(
				"https://kapi.kakao.com/v2/user/me", kakaoProfileRequest,
				String.class);
			log.info("kapi 성공: {}", kakaoProfileResponse.getBody());
		} catch (RestClientException e) {
			log.error("kapi 실패 {}", e.getMessage());
			throw new RuntimeException(e);
		}

		ObjectMapper objectMapper2 = new ObjectMapper();
		objectMapper2.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		Object kakaoProfile = null;

		try {
			kakaoProfile = objectMapper2.readValue(kakaoProfileResponse.getBody(), Object.class);
			log.info("Kakao profile: {}", kakaoProfile.toString());
		} catch (JsonProcessingException e) {
			log.error("Error parsing kakao profile: {}", kakaoProfileResponse);
			throw new RuntimeException(e);
		}

		JwtToken jwt = jwtUtil.createJwt(1L, 30000L);
		return ResponseEntity.ok(jwt);
	}

	@PostMapping("/api/onboarding")
	public ResponseEntity<Void> createOnboarding(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody @Valid CreateOnboardingRequest createOnboardingRequest
	) {
		Long memberId = customOAuth2User.getId();
		memberService.createOnboarding(memberId, createOnboardingRequest);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/profile/{memberId}")
	public ResponseEntity<ProfileResponse> getProfile(@PathVariable("memberId") Long memberId) {

		return ResponseEntity.ok(memberService.getProfile(memberId));
	}

	@GetMapping("/api/profile")
	public ResponseEntity<SelfProfileResponse> getSelfProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		Long memberId = customOAuth2User.getId();

		return ResponseEntity.ok(memberService.getSelfProfile(memberId));
	}

	@PatchMapping("/api/profile")
	public ResponseEntity<UpdateProfileResponse> updateProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestPart(value = "updateProfile") UpdateProfileRequest updateProfileRequest,
		@RequestPart(value = "imageFile") MultipartFile imageFile) {

		Long memberId = customOAuth2User.getId();

		return ResponseEntity.ok(memberService.updateProfile(memberId, updateProfileRequest, imageFile));
	}
}
