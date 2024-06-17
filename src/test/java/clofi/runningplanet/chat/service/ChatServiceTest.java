package clofi.runningplanet.chat.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.runningplanet.chat.domain.Chat;
import clofi.runningplanet.chat.dto.request.ChatMessageRequest;
import clofi.runningplanet.chat.dto.response.ChatListResponse;
import clofi.runningplanet.chat.dto.response.ChatMessageResponse;
import clofi.runningplanet.chat.repository.ChatRepository;
import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.domain.Role;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;

@SpringBootTest
class ChatServiceTest {

	@Autowired
	ChatService chatService;

	@Autowired
	ChatRepository chatRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CrewRepository crewRepository;

	@Autowired
	CrewMemberRepository crewMemberRepository;

	@AfterEach
	void tearDown() {
		chatRepository.deleteAllInBatch();
		crewMemberRepository.deleteAllInBatch();
		crewRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("채팅 저장 테스트")
	@Test
	void saveChatTest() {
		//given
		Member member1 = new Member(null, "turtle", Gender.MALE, 20, 100, "profileImg1", 10, 300, 250, 1000);

		memberRepository.save(member1);

		Crew crew = new Crew(member1.getId(), "crew1", 10, Category.RUNNING, ApprovalType.AUTO, "introduction", 7, 1);

		crewRepository.save(crew);

		CrewMember crewMember1 = new CrewMember(null, crew, member1, Role.LEADER);

		crewMemberRepository.save(crewMember1);

		ChatMessageRequest chatMessageRequest = new ChatMessageRequest(member1.getNickname(), "Hi");

		//when
		ChatMessageResponse savedChat = chatService.saveChatMessage(member1.getId(), crew.getId(), chatMessageRequest);

		//then
		assertThat(savedChat.from()).isEqualTo(member1.getNickname());
		assertThat(savedChat.message()).isEqualTo(chatMessageRequest.message());
	}

	@DisplayName("등록된 채팅을 불러올 수 있다.")
	@Test
	void getChatMessagesTest() {
		//given
		Member member1 = new Member(null, "turtle", Gender.MALE, 20, 100, "profileImg1", 10, 300, 250, 1000);
		Member member2 = new Member(null, "rabbit", Gender.FEMALE, 25, 70, "profileImg2", 10, 600, 250, 1000);

		memberRepository.save(member1);
		memberRepository.save(member2);

		Crew crew = new Crew(member1.getId(), "crew1", 10, Category.RUNNING, ApprovalType.AUTO, "introduction", 7, 1);

		crewRepository.save(crew);

		CrewMember crewMember1 = new CrewMember(null, crew, member1, Role.LEADER);
		CrewMember crewMember2 = new CrewMember(null, crew, member2, Role.MEMBER);

		crewMemberRepository.save(crewMember1);
		crewMemberRepository.save(crewMember2);


		Chat chat1 = new Chat(null, member1, crew, "I want your liver");
		Chat chat2 = new Chat(null, member2, crew, "I don't have it. I'll bring it");
		Chat chat3 = new Chat(null, member1, crew, "Ok, Let's go to land");

		Chat saveChat1 = chatRepository.save(chat1);
		Chat saveChat2 = chatRepository.save(chat2);
		Chat saveChat3 = chatRepository.save(chat3);

		LocalDateTime lastChatDateTime = saveChat3.getCreatedAt();

		// 밀리초를 6자리로 자르기 위해 초와 나노초를 가져옴
		long nanoSeconds = lastChatDateTime.getLong(ChronoField.NANO_OF_SECOND);
		long microSeconds = nanoSeconds / 1000; // 나노초를 마이크로초로 변환

		// LocalDateTime에서 나노초 대신 마이크로초를 사용
		LocalDateTime formattedDateTime = lastChatDateTime.withNano((int)(microSeconds * 1000));

		// 새로운 포맷터 생성
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

		// 포맷 후 출력
		String formattedDate = formattedDateTime.format(formatter);

		//when
		ChatListResponse chatList = chatService.getChatMessages(member1.getId(), crew.getId(), null, 10);
		ChatListResponse chatList2 = chatService.getChatMessages(member2.getId(), crew.getId(), null, 1);
		ChatListResponse chatList3 = chatService.getChatMessages(member1.getId(), crew.getId(), formattedDate, 10);

		//then
		assertThat(chatList.chatArray().size()).isEqualTo(3);

		assertThat(chatList.chatArray().get(0).from()).isEqualTo("turtle");
		assertThat(chatList.chatArray().get(0).message()).isEqualTo("Ok, Let's go to land");
		assertThat(chatList.chatArray().get(1).from()).isEqualTo("rabbit");
		assertThat(chatList.chatArray().get(1).message()).isEqualTo("I don't have it. I'll bring it");
		assertThat(chatList.chatArray().get(2).from()).isEqualTo("turtle");
		assertThat(chatList.chatArray().get(2).message()).isEqualTo("I want your liver");

		assertThat(chatList.existsNextPage()).isFalse();
		assertThat(chatList2.existsNextPage()).isTrue();

		assertThat(chatList3.chatArray().get(0).message()).isEqualTo("I don't have it. I'll bring it");
		assertThat(chatList3.chatArray().size()).isEqualTo(2);
		assertThat(chatList3.existsNextPage()).isFalse();
	}

}