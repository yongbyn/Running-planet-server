package clofi.runningplanet.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.chat.dto.response.ChatListResponse;
import clofi.runningplanet.chat.dto.response.ChatMessage;
import clofi.runningplanet.chat.dto.response.DataResponse;
import clofi.runningplanet.chat.service.ChatService;
import clofi.runningplanet.member.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@MessageMapping("/crew/{crewId}/chat")
	@SendTo("/sub/crew/{crewId}/chat")
	public DataResponse<ChatMessage> sendChatMessage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@DestinationVariable Long crewId,
		@Payload ChatMessage chatMessage
	) {
		Long memberId = customOAuth2User.getId();
		ChatMessage savedChat = chatService.saveChatMessage(memberId,crewId, chatMessage);
		return new DataResponse<>(savedChat);
	}

	@GetMapping("/api/crew/{crewId}/chat")
	public ResponseEntity<ChatListResponse> getChatMessages(
		@PathVariable Long crewId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "30") int size
	) {
		ChatListResponse chatList = chatService.getChatMessages(crewId, page, size);

		return ResponseEntity.ok(chatList);
	}
}
