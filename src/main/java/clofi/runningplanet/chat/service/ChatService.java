package clofi.runningplanet.chat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.chat.domain.Chat;
import clofi.runningplanet.chat.dto.response.ChatListResponse;
import clofi.runningplanet.chat.dto.response.ChatMessage;
import clofi.runningplanet.chat.repository.ChatRepository;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final MemberRepository memberRepository;
	private final CrewRepository crewRepository;

	public ChatMessage saveChatMessage(Long memberId, Long crewId, ChatMessage chatMessage) {

		Member member = getMember(memberId, chatMessage);
		Crew crew = getCrew(crewId);

		Chat chat = Chat.builder()
			.member(member)
			.crew(crew)
			.content(chatMessage.message())
			.build();

		chatRepository.save(chat);

		return new ChatMessage(chatMessage.from(), chatMessage.message(), chat.getCreatedAt());
	}

	private Member getMember(Long memberId, ChatMessage chatMessage) {
		return memberRepository.findByNicknameAndMemberId(chatMessage.from(), memberId)
			.orElseThrow(() -> new RuntimeException("일치하는 사용자가 없습니다."));
	}

	private Crew getCrew(Long crewId) {
		return crewRepository.findById(crewId)
			.orElseThrow(() -> new RuntimeException("일치하는 크루가 없습니다."));
	}

	public ChatListResponse getChatMessages(Long crewId, int page, int size) {

		validateSizeIsPositive(size);

		Pageable pageable = PageRequest.of(page, size);
		Page<Chat> chatPage = chatRepository.findByCrewId(crewId, pageable);

		List<ChatMessage> chatList = getChatList(chatPage);

		boolean existsNextPage = chatPage.hasNext();

		return new ChatListResponse(chatList, existsNextPage);
	}

	private static void validateSizeIsPositive(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("설정할 채팅의 개수는 양수이어야 합니다.");
		}
	}

	private static List<ChatMessage> getChatList(Page<Chat> chatPage) {
		return chatPage.stream()
			.map(chat -> new ChatMessage(chat.getMember().getNickname(), chat.getContent(), chat.getCreatedAt()))
			.toList();
	}
}
