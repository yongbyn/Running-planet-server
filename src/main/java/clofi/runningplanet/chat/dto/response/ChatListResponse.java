package clofi.runningplanet.chat.dto.response;

import java.util.List;

public record ChatListResponse(
	List<ChatMessage> chatArray,
	boolean existsNestPage
) {
}
