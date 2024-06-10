package clofi.runningplanet.socket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import clofi.runningplanet.socket.dto.response.ChatMessage;
import clofi.runningplanet.socket.dto.response.DataResponse;
import clofi.runningplanet.socket.service.SocketService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SocketController {

	private final SocketService socketService;

	@MessageMapping("/crew/{crewId}/chat")
	@SendTo("/sub/crew/{crewId}/chat")
	public DataResponse<ChatMessage> sendChatMessage(
		@DestinationVariable Long crewId,
		@Payload ChatMessage chatMessage
	){
		ChatMessage savedChat = socketService.saveAndReturnChatMessage(crewId, chatMessage);
		return new DataResponse<>(chatMessage);
	}
}