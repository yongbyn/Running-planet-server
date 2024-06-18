package clofi.runningplanet.crew.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.crew.dto.SearchParamDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.ProceedApplyReqDto;
import clofi.runningplanet.crew.dto.request.UpdateCrewReqDto;
import clofi.runningplanet.crew.dto.response.ApplyCrewResDto;
import clofi.runningplanet.crew.dto.response.ApprovalMemberResDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewMemberResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewWithMissionResDto;
import clofi.runningplanet.crew.service.CrewService;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class CrewController {

	private final CrewService crewService;

	@PostMapping("/api/crew")
	public ResponseEntity<Void> createCrew(@RequestPart("crewInfo") @Valid CreateCrewReqDto reqDto,
		@RequestPart(value = "imgFile") MultipartFile imageFile,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		Long crewId = crewService.createCrew(reqDto, imageFile, principal.getId());
		return ResponseEntity.created(URI.create("/api/crew/" + crewId)).build();
	}

	@GetMapping("/api/crew")
	public ResponseEntity<List<FindAllCrewResDto>> findAllCrews(
		@RequestParam(name = "crewName", required = false) String crewName,
		@RequestParam(name = "category", required = false) String category) {
		SearchParamDto searchParamDto = new SearchParamDto(crewName, category);
		return ResponseEntity.ok(crewService.findAllCrew(searchParamDto));
	}

	@GetMapping("/api/crew/{crewId}")
	public ResponseEntity<FindCrewResDto> findCrew(@PathVariable("crewId") Long crewId) {
		return ResponseEntity.ok(crewService.findCrew(crewId));
	}

	@PostMapping("/api/crew/{crewId}")
	public ResponseEntity<ApplyCrewResDto> applyCrew(@PathVariable("crewId") Long crewId,
		@RequestBody ApplyCrewReqDto reqDto,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		return ResponseEntity.status(HttpStatus.CREATED).body(crewService.applyCrew(reqDto, crewId, principal.getId()));
	}

	@GetMapping("/api/crew/{crewId}/request")
	public ResponseEntity<ApprovalMemberResDto> getApplyCrewList(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		return ResponseEntity.ok(crewService.getApplyCrewList(crewId, principal.getId()));
	}

	@PostMapping("/api/crew/{crewId}/request")
	public ResponseEntity<Void> proceedApplyCrew(@PathVariable("crewId") Long crewId,
		@RequestBody ProceedApplyReqDto reqDto, @AuthenticationPrincipal CustomOAuth2User principal) {
		crewService.proceedApplyCrew(reqDto, crewId, principal.getId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/crew/{crewId}/{memberId}")
	public ResponseEntity<Void> removeCrewMember(@PathVariable("crewId") Long crewId,
		@PathVariable("memberId") Long memberId, @AuthenticationPrincipal CustomOAuth2User principal) {
		crewService.removeCrewMember(crewId, memberId, principal.getId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/crew/{crewId}")
	public ResponseEntity<Void> leaveCrew(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		crewService.leaveCrew(crewId, principal.getId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/crew/{crewId}/request")
	public ResponseEntity<ApplyCrewResDto> cancelCrewApplication(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		return ResponseEntity.ok(crewService.cancelCrewApplication(crewId, principal.getId()));
	}

	@PatchMapping("/api/crew/{crewId}")
	public ResponseEntity<Void> updateCrew(@RequestPart("modifyInfo") @Valid UpdateCrewReqDto reqDto,
		@RequestPart(value = "imgFile", required = false) MultipartFile imgFile, @PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		crewService.updateCrew(reqDto, imgFile, crewId, principal.getId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/crew/{crewId}/page")
	public ResponseEntity<FindCrewWithMissionResDto> findCrewWithMission(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		return ResponseEntity.ok(crewService.findCrewWithMission(crewId, principal.getId()));
	}

	@GetMapping("/api/crew/{crewId}/member")
	public ResponseEntity<List<FindCrewMemberResDto>> findCrewMemberList(@PathVariable("crewId") Long crewId,
		@AuthenticationPrincipal CustomOAuth2User principal) {
		return ResponseEntity.ok(crewService.findCrewMemberList(crewId, principal.getId()));
	}
}
