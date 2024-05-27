package clofi.runningplanet.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.member.dto.ProfileResponse;
import clofi.runningplanet.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/api/profile/{memberId}")
	public ResponseEntity<ProfileResponse> getProfile(@PathVariable("memberId") Long memberId) {

		return ResponseEntity.ok(memberService.getProfile(memberId));
	}

}
