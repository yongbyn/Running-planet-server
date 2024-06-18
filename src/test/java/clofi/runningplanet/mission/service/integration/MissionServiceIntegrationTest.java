package clofi.runningplanet.mission.service.integration;

import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import clofi.runningplanet.common.DataCleaner;
import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.service.CrewService;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.service.MissionService;

@SpringBootTest
public class MissionServiceIntegrationTest {

	@Autowired
	MissionService missionService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CrewRepository crewRepository;

	@Autowired
	CrewMemberRepository crewMemberRepository;

	@Autowired
	CrewService crewService;

	@Autowired
	DataCleaner cleaner;

	@AfterEach
	void setUp() {
		cleaner.truncateAllTables();
	}

	@DisplayName("크루를 생성하면 크루 미션 2개가 생성된다.")
	@Test
	void createCrewCreate2CrewMission() {
		//given
		Long memberId = saveMember1();
		// Long crewId = createCrew(memberId);

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.AUTO,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId);

		//when
		CrewMissionListDto result = missionService.getCrewMission(crewId, memberId);

		//then
		assertSoftly(softly -> {
			softly.assertThat(result.missions()).hasSize(2)
				.extracting("missionContent")
				.containsExactlyInAnyOrder(MissionType.DURATION, MissionType.DISTANCE);

			softly.assertThat(result.missions())
				.extracting("missionProgress")
				.allMatch(progress -> progress.equals(0.0));

			softly.assertThat(result.missions())
				.extracting("missionComplete")
				.allMatch(complete -> complete.equals(false));
		});

	}

	private Long saveMember1() {
		Member member1 = Member.builder()
			.nickname("크루장")
			.profileImg("https://test.com")
			.gender(Gender.MALE)
			.age(30)
			.weight(70)
			.build();
		return memberRepository.save(member1).getId();
	}

	private Long createCrew(Long memberId) {
		Crew crew = new Crew(null, saveMember1(), "구름", 10, Category.RUNNING, ApprovalType.AUTO, "크루 소개", 3, 1, 0, 0, 0,
			1);
		Crew savedCrew = crewRepository.save(crew);

		Member leader = memberRepository.findById(memberId).get();
		CrewMember crewMember = CrewMember.createLeader(savedCrew, leader);
		crewMemberRepository.save(crewMember);

		return savedCrew.getId();
	}
}
