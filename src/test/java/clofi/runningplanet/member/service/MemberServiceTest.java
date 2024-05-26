package clofi.runningplanet.member.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.domain.Role;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.dto.ProfileResponse;
import clofi.runningplanet.member.repository.MemberRepository;

@SpringBootTest
class MemberServiceTest {
	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CrewMemberRepository crewMemberRepository;

	@Autowired
	CrewRepository crewRepository;

	@AfterEach
	void tearDown() {
		crewMemberRepository.deleteAllInBatch();
		crewRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("memberId로 조회할 수 있다.")
	@Test
	void getProfilWithCrew() {
		//given
		//크루 있는 경우
		Member member1 = Member.builder()
			.nickname("고구마1")
			.profileImg(
				"https://pbs.twimg.com/media/E86TJH1VkAQ0BGV.png")
			.age(34)
			.gender(Gender.MALE)
			.runScore(20)
			.avgPace(2400)
			.avgDistance(5000)
			.totalDistance(30000)
			.build();
		memberRepository.save(member1);

		Crew crew1 = new Crew(
			member1.getId(),"고구마크루" ,5,10, Category.RUNNING, ApprovalType.AUTO,"소개글",7,1);
		crewRepository.save(crew1);

		CrewMember crewMember1 = CrewMember.builder()
			.member(member1)
			.role(Role.LEADER)
			.crew(crew1)
			.build();
		crewMemberRepository.save(crewMember1);

		//크루 없는 경우
		Member member2 = memberRepository.save(createMember());

		//when
		ProfileResponse profileResponseWithCrew = memberService.getProfile(member1.getId()).getBody();
		ProfileResponse profileResponseWithoutCrew = memberService.getProfile(member2.getId()).getBody();


		//then
		assertNotNull(profileResponseWithCrew);
		assertEquals(member1.getNickname(), profileResponseWithCrew.nickname());
		assertEquals("고구마크루", profileResponseWithCrew.myCrew());

		assertNotNull(profileResponseWithoutCrew);
		assertEquals(member2.getNickname(), profileResponseWithoutCrew.nickname());
		assertEquals("없음", profileResponseWithoutCrew.myCrew());;
	}


	private Member createMember() {
		return Member.builder()
			.nickname("고구마")
			.profileImg(
				"https://pbs.twimg.com/media/E86TJH1VkAQ0BGV.png")
			.age(34)
			.gender(Gender.MALE)
			.runScore(20)
			.avgPace(2400)
			.avgDistance(5000)
			.totalDistance(30000)
			.build();
	}
}