package clofi.runningplanet.member.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.member.dto.request.CreateOnboardingRequest;
import clofi.runningplanet.member.dto.request.UpdateProfileRequest;
import clofi.runningplanet.member.dto.response.ProfileResponse;
import clofi.runningplanet.member.dto.response.SelfProfileResponse;
import clofi.runningplanet.member.dto.response.UpdateProfileResponse;
import clofi.runningplanet.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/api/login/{provider}")
	public void login(@PathVariable("provider") String provider, HttpServletResponse response) throws IOException {
		String redirectUrl = switch (provider.toLowerCase()) {
			case "kakao" -> "/oauth2/authorization/kakao";
			case "naver" -> "/oauth2/authorization/naver";
			case "google" -> "/oauth2/authorization/google";
			default -> throw new IllegalArgumentException("잘못된 소셜로그인입니다. 요청한 소셜로그인 : " + provider);
		};
		response.sendRedirect(redirectUrl);
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
		@RequestPart(value = "imgFile", required = false) MultipartFile imgFile) {

		Long memberId = customOAuth2User.getId();

		return ResponseEntity.ok(memberService.updateProfile(memberId, updateProfileRequest, imgFile));
	}
}
