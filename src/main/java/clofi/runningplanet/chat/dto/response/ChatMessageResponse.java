package clofi.runningplanet.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
	String from,
	String message,
	LocalDateTime time
) {
}
