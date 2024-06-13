package clofi.runningplanet.running.controller;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.member.dto.CustomOAuth2User;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.RecordFindAllResponse;
import clofi.runningplanet.running.dto.RecordFindCurrentResponse;
import clofi.runningplanet.running.dto.RecordFindResponse;
import clofi.runningplanet.running.dto.RecordSaveRequest;
import clofi.runningplanet.running.dto.RecordSaveResponse;
import clofi.runningplanet.running.dto.RunningStatusFindAllResponse;
import clofi.runningplanet.running.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RecordController {
	private final RecordService recordService;

	@PostMapping("/record")
	public RecordSaveResponse saveRecord(
		@RequestBody @Valid RecordSaveRequest request,
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		Record record = recordService.save(request, user.getId());
		return new RecordSaveResponse(record.getId());
	}

	@GetMapping("/record")
	public List<RecordFindAllResponse> findAllRecords(
		@RequestParam Integer year,
		@RequestParam Integer month,
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		return recordService.findAll(year, month, user.getId());
	}

	@GetMapping("/record/{recordId}")
	public RecordFindResponse getRecord(
		@PathVariable("recordId") Long recordId,
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		return recordService.find(recordId, user.getId());
	}

	@GetMapping("/record/current")
	public RecordFindCurrentResponse getCurrentRecord(@AuthenticationPrincipal CustomOAuth2User user) {
		return recordService.findCurrentRecord(user.getId());
	}

	@GetMapping("/crew/{crewId}/running")
	public List<RunningStatusFindAllResponse> findAllRunningStatus(
		@PathVariable("crewId") Long crewId, @AuthenticationPrincipal CustomOAuth2User user
	) {
		return recordService.findAllRunningStatus(user.getId(), crewId);
	}

	@PostMapping("/crew/{crewId}/cheer")
	public void cheering(@PathVariable("crewId") Long crewId, @RequestBody Set<Long> toMemberIds,
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		recordService.sendCheering(crewId, user.getId(), toMemberIds);
	}

}
