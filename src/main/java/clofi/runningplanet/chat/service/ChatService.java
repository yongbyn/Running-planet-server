package clofi.runningplanet.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.chat.domain.Chat;
import clofi.runningplanet.chat.dto.request.ChatMessageRequest;
import clofi.runningplanet.chat.dto.response.ChatListResponse;
import clofi.runningplanet.chat.dto.response.ChatMessageResponse;
import clofi.runningplanet.chat.repository.ChatRepository;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

	private final ChatRepository chatRepository;
	private final MemberRepository memberRepository;
	private final CrewRepository crewRepository;
	private final CrewMemberRepository crewMemberRepository;

	public ChatMessageResponse saveChatMessage(Long memberId, Long crewId, ChatMessageRequest chatMessageRequest) {

		Member member = getMember(memberId, chatMessageRequest);
		Crew crew = getCrew(crewId);
		validateMemberIsInCrew(memberId, crewId);

		Chat chat = Chat.builder()
			.member(member)
			.crew(crew)
			.content(chatMessageRequest.message())
			.build();

		Chat saveChat = chatRepository.save(chat);

		return new ChatMessageResponse(chatMessageRequest.from(), chatMessageRequest.message(), saveChat.getCreatedAt());
	}

	public ChatListResponse getChatMessages(Long memberId, Long crewId, String lastChatTimestamp, int size) {

		validateMemberIsInCrew(memberId, crewId);
		validateSizeIsPositive(size);

		Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
		Page<Chat> chatPage;

		chatPage = getChatPageByCrewIdAndLastChatTimestamp(crewId, lastChatTimestamp, pageable);

		List<ChatMessageResponse> chatList = getChatList(chatPage);

		boolean existsNextPage = chatPage.hasNext();

		return new ChatListResponse(chatList, existsNextPage);
	}


	private Member getMember(Long memberId, ChatMessageRequest chatMessageRequest) {
		return memberRepository.findByIdAndNickname(memberId, chatMessageRequest.from())
			.orElseThrow(() -> new RuntimeException("일치하는 사용자가 없습니다."));
	}

	private Crew getCrew(Long crewId) {
		return crewRepository.findById(crewId)
			.orElseThrow(() -> new RuntimeException("일치하는 크루가 없습니다."));
	}

	private void validateMemberIsInCrew(Long memberId, Long crewId) {
		boolean memberInCrew = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent();
		if(!memberInCrew) {
			throw new RuntimeException("해당 사용자는 크루에 속해 있지 않습니다.");
		}
	}

	private static void validateSizeIsPositive(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("설정할 채팅의 개수는 양수이어야 합니다.");
		}
	}

	private Page<Chat> getChatPageByCrewIdAndLastChatTimestamp(Long crewId, String lastChatTimestamp, Pageable pageable) {
		Page<Chat> chatPage;
		if (lastChatTimestamp == null) {
			chatPage = chatRepository.findByCrewId(crewId, pageable);
		} else {
			LocalDateTime lastChatDateTime = LocalDateTime.parse(lastChatTimestamp);

			chatPage = chatRepository.findByCrewIdAndCreatedAtLessThan(crewId, lastChatDateTime, pageable);
		}
		return chatPage;
	}

	private static List<ChatMessageResponse> getChatList(Page<Chat> chatPage) {
		return chatPage.stream()
			.map(chat -> new ChatMessageResponse(chat.getMember().getNickname(), chat.getContent(), chat.getCreatedAt()))
			.toList();
	}
}
