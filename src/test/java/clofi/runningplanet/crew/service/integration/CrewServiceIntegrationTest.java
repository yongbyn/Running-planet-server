package clofi.runningplanet.crew.service.integration;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import clofi.runningplanet.common.DatabaseCleaner;
import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.ProceedApplyReqDto;
import clofi.runningplanet.crew.dto.response.ApplyCrewResDto;
import clofi.runningplanet.crew.dto.response.ApprovalMemberResDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.service.CrewService;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;

@SpringBootTest
public class CrewServiceIntegrationTest {

	@Autowired
	CrewService crewService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	DatabaseCleaner cleaner;

	@BeforeEach
	void init() {
		Member member1 = Member.builder()
			.nickname("크루장")
			.id(1L)
			.profileImg("https://test.com")
			.gender(Gender.MALE)
			.age(30)
			.weight(70)
			.build();
		memberRepository.save(member1);

		Member member2 = Member.builder()
			.nickname("크루")
			.id(2L)
			.profileImg("https://test.com")
			.gender(Gender.FEMALE)
			.age(20)
			.weight(60)
			.build();
		memberRepository.save(member2);
	}

	@AfterEach
	void setUp() {
		cleaner.truncateAllTables();
	}

	@DisplayName("크루를 생성 테스트 코드")
	@Test
	void createCrew() {
		//given
		Long memberId = 1L;

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.AUTO,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		//when
		Long crewId = crewService.createCrew(reqDto, image, memberId);

		//then
		assertThat(crewId).isNotNull();
	}

	@DisplayName("크루 목록을 조회 할 수 있다.")
	@Test
	void findAllCrews() {
		//given

		CreateCrewReqDto reqDto1 = new CreateCrewReqDto("크루명1", Category.RUNNING, List.of("태그1"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image1 = new MockMultipartFile("imgFile", "크루로고1.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고1.png".getBytes());

		Long crewId1 = crewService.createCrew(reqDto1, image1, 1L);

		CreateCrewReqDto reqDto2 = new CreateCrewReqDto("크루명2", Category.RUNNING, List.of("태그2"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image2 = new MockMultipartFile("imgFile", "크루로고2.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고2.png".getBytes());

		Long crewId2 = crewService.createCrew(reqDto2, image2, 2L);

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew();

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result.size()).isEqualTo(2);
				softAssertions.assertThat(result).extracting("crewId")
					.contains(crewId1, crewId2);
			}
		);

	}

	@DisplayName("crewId를 통해 크루 정보를 조회할 수 있다.")
	@Test
	void findCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.AUTO,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		Long crewId = crewService.createCrew(reqDto, image, 1L);

		//when
		FindCrewResDto result = crewService.findCrew(crewId);

		//then
		assertSoftly(
			softly -> {
				softly.assertThat(result.crewId()).isEqualTo(crewId);
				softly.assertThat(result)
					.extracting("crewLevel", "memberCnt", "limitMemberCnt", "crewTotalDistance")
					.containsExactly(1, 1, 10, 0);
				softly.assertThat(result)
					.extracting("crewName", "approvalType", "introduction", "category")
					.containsExactly("크루명", ApprovalType.AUTO, "크루 소개", Category.RUNNING);
			}
		);
	}

	@DisplayName("crewId를 통해 원하는 크루에 신청할 수 있다.")
	@Test
	void applyCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");

		//when
		ApplyCrewResDto result = crewService.applyCrew(applyReqDto, crewId, 2L);

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result)
					.extracting("crewId", "memberId")
					.containsExactly(crewId, 2L);
				softAssertions.assertThat(result.isRequest()).isTrue();
			}
		);

	}

	@DisplayName("크루장은 크루에 신청한 사용자 목록을 확인할 수 있다.")
	@Test
	void getCrewApplicationList() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, 2L);

		//when
		ApprovalMemberResDto result = crewService.getApplyCrewList(crewId, 1L);

		//then
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.approvalMember().size()).isEqualTo(1);
			softAssertions.assertThat(result.approvalMember().getFirst())
				.extracting("memberId", "nickname", "introduction", "gender", "age")
				.containsExactly(2L, "크루", "크루 가입 신청서", Gender.FEMALE, 20);
		});

	}

	@DisplayName("크루에 신청한 인원이 없는 경우 빈 리스트를 반환한다.")
	@Test
	void getCrewApplicationEmptyList() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		//when
		ApprovalMemberResDto result = crewService.getApplyCrewList(crewId, 1L);

		//then
		assertThat(result.approvalMember()).isEmpty();

	}

	@DisplayName("크루장은 크루 신청인원을 수락할 수 있다.")
	@Test
	void proceedApplicationCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, 2L);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(2L, true);

		//when
		//then
		assertDoesNotThrow(() -> crewService.proceedApplyCrew(proceedApplyReqDto, crewId, 1L));
	}

	@DisplayName("크루장은 크루 신청인원을 거절할 수 있다.")
	@Test
	void rejectApplicationCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, 2L);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(2L, false);

		//when
		//then
		assertDoesNotThrow(() -> crewService.proceedApplyCrew(proceedApplyReqDto, crewId, 1L));
	}

	@DisplayName("크루장은 크루원을 강퇴할 수 있다.")
	@Test
	void removeCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, 2L);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(2L, true);
		crewService.proceedApplyCrew(proceedApplyReqDto, crewId, 1L);

		//when
		//then
		assertDoesNotThrow(() -> crewService.removeCrewMember(crewId, 2L, 1L));
	}

	@DisplayName("크루원은 크루를 탈퇴할 수 있다.")
	@Test
	void leaveCrew() {
		//given
		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, 1L);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, 2L);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(2L, true);
		crewService.proceedApplyCrew(proceedApplyReqDto, crewId, 1L);

		//when
		//then
		assertDoesNotThrow(() -> crewService.leaveCrew(crewId, 2L));
	}
}
