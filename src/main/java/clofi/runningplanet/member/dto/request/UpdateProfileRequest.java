package clofi.runningplanet.member.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileRequest(
	String nickname
) {
}
