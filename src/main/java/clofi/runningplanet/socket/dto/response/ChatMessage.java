package clofi.runningplanet.socket.dto.response;

import org.joda.time.DateTime;

public record ChatMessage(
	String from,
	String message,
	DateTime time
) {}
