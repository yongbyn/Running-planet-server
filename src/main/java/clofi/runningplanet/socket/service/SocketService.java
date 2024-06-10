package clofi.runningplanet.socket.service;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.crew.domain.Chat;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.ChatRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.socket.dto.response.ChatMessage;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SocketService {

	private final ChatRepository chatRepository;
	private final MemberRepository memberRepository;
	private final CrewRepository crewRepository;

	public ChatMessage saveAndReturnChatMessage(Long crewId, ChatMessage chatMessage) {
		Member member = memberRepository.findByNickname(chatMessage.from())
			.orElseThrow(() -> new RuntimeException("일치하는 사용자가 없습니다."));
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> new RuntimeException("일치하는 크루가 없습니다."));

		Chat chat = Chat.builder()
			.member(member)
			.crew(crew)
			.content(chatMessage.message())
			.build();

		chatRepository.save(chat);

		return new ChatMessage(chatMessage.from(), chatMessage.message(), new DateTime(chat.getCreatedAt()));
	}

}
