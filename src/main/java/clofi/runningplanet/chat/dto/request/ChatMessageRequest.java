package clofi.runningplanet.chat.dto.request;

public record ChatMessageRequest(
	String from,
	String message
) {
}
