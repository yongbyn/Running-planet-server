package clofi.runningplanet.running.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.RecordSaveRequest;
import clofi.runningplanet.running.dto.RecordSaveResponse;
import clofi.runningplanet.running.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class RecordController {
	private final RecordService recordService;

	//TODO: 로그인 기능 구현 후 Principal 사용하게 변경하기.
	@PostMapping("/record")
	public ResponseEntity<RecordSaveResponse> saveRecord(@RequestBody @Valid RecordSaveRequest request) {
		Record record = recordService.save(request);
		return ResponseEntity.ok(new RecordSaveResponse(record.getId()));
	}

}
