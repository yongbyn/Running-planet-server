package clofi.runningplanet.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessage(
	String from,
	String message,
	LocalDateTime time
) {
}
