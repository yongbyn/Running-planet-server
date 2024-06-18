package clofi.runningplanet.crew.service.integration;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import clofi.runningplanet.common.DataCleaner;
import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.SearchParamDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.ProceedApplyReqDto;
import clofi.runningplanet.crew.dto.request.UpdateCrewReqDto;
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
	DataCleaner cleaner;

	@AfterEach
	void setUp() {
		cleaner.truncateAllTables();
	}

	@DisplayName("크루를 생성 테스트 코드")
	@Test
	void createCrew() {
		//given
		Long memberId = saveMember1();

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
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto1 = new CreateCrewReqDto("크루명1", Category.RUNNING, List.of("태그1"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image1 = new MockMultipartFile("imgFile", "크루로고1.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고1.png".getBytes());

		Long crewId1 = crewService.createCrew(reqDto1, image1, memberId1);

		CreateCrewReqDto reqDto2 = new CreateCrewReqDto("크루명2", Category.RUNNING, List.of("태그2"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image2 = new MockMultipartFile("imgFile", "크루로고2.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고2.png".getBytes());

		Long crewId2 = crewService.createCrew(reqDto2, image2, memberId2);

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew(new SearchParamDto("", null));

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result.size()).isEqualTo(2);
				softAssertions.assertThat(result).extracting("crewId")
					.contains(crewId1, crewId2);
			}
		);

	}

	@DisplayName("카테고리를 통해 크루 목록을 필터링 할 수 있다.")
	@Test
	void filterCrewByCategory() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();
		Long memberId3 = saveMember1();

		CreateCrewReqDto reqDto1 = new CreateCrewReqDto("구름", Category.RUNNING, List.of("태그1"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image1 = new MockMultipartFile("imgFile", "크루로고1.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고1.png".getBytes());

		Long crewId1 = crewService.createCrew(reqDto1, image1, memberId1);

		CreateCrewReqDto reqDto2 = new CreateCrewReqDto("클로피", Category.DIET, List.of("태그2"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image2 = new MockMultipartFile("imgFile", "크루로고2.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고2.png".getBytes());

		Long crewId2 = crewService.createCrew(reqDto2, image2, memberId2);

		CreateCrewReqDto reqDto3 = new CreateCrewReqDto("구름 클로피", Category.RUNNING, List.of("태그3"), ApprovalType.MANUAL,
			"크루 소개3", new RuleDto(3, 10));
		MockMultipartFile image3 = new MockMultipartFile("imgFile", "크루로고3.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고3.png".getBytes());

		Long crewId3 = crewService.createCrew(reqDto3, image3, memberId3);

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew(new SearchParamDto("", "Running"));

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result.size()).isEqualTo(2);
				softAssertions.assertThat(result).extracting("crewId")
					.contains(crewId1, crewId3)
					.doesNotContain(crewId2);
				softAssertions.assertThat(result).extracting("category")
					.allMatch(category -> category.equals(Category.RUNNING));
			}
		);

	}

	@DisplayName("검색어를 통해 크루 이름을 포함하는 크루를 검색 할 수 있다.")
	@Test
	void searchCrewByCrewName() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();
		Long memberId3 = saveMember1();

		CreateCrewReqDto reqDto1 = new CreateCrewReqDto("구름", Category.RUNNING, List.of("태그1"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image1 = new MockMultipartFile("imgFile", "크루로고1.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고1.png".getBytes());

		Long crewId1 = crewService.createCrew(reqDto1, image1, memberId1);

		CreateCrewReqDto reqDto2 = new CreateCrewReqDto("클로피", Category.DIET, List.of("태그2"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image2 = new MockMultipartFile("imgFile", "크루로고2.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고2.png".getBytes());

		Long crewId2 = crewService.createCrew(reqDto2, image2, memberId2);

		CreateCrewReqDto reqDto3 = new CreateCrewReqDto("구름 클로피", Category.RUNNING, List.of("태그3"), ApprovalType.MANUAL,
			"크루 소개3", new RuleDto(3, 10));
		MockMultipartFile image3 = new MockMultipartFile("imgFile", "크루로고3.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고3.png".getBytes());

		Long crewId3 = crewService.createCrew(reqDto3, image3, memberId3);

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew(new SearchParamDto("클로피", ""));

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result.size()).isEqualTo(2);
				softAssertions.assertThat(result).extracting("crewId")
					.contains(crewId2, crewId3)
					.doesNotContain(crewId1);
				softAssertions.assertThat(result).extracting("crewName")
					.allMatch(crewName -> String.valueOf(crewName).contains("클로피"));
			}
		);

	}

	@DisplayName("크루명 검색 및 카테고리 필터링을 동시에 할 수 있다.")
	@Test
	void searchAndFilteringCrew() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();
		Long memberId3 = saveMember1();

		CreateCrewReqDto reqDto1 = new CreateCrewReqDto("구름", Category.RUNNING, List.of("태그1"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image1 = new MockMultipartFile("imgFile", "크루로고1.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고1.png".getBytes());

		Long crewId1 = crewService.createCrew(reqDto1, image1, memberId1);

		CreateCrewReqDto reqDto2 = new CreateCrewReqDto("클로피", Category.DIET, List.of("태그2"), ApprovalType.AUTO,
			"크루 소개2", new RuleDto(3, 10));
		MockMultipartFile image2 = new MockMultipartFile("imgFile", "크루로고2.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고2.png".getBytes());

		Long crewId2 = crewService.createCrew(reqDto2, image2, memberId2);

		CreateCrewReqDto reqDto3 = new CreateCrewReqDto("구름 클로피", Category.RUNNING, List.of("태그3"), ApprovalType.MANUAL,
			"크루 소개3", new RuleDto(3, 10));
		MockMultipartFile image3 = new MockMultipartFile("imgFile", "크루로고3.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고3.png".getBytes());

		Long crewId3 = crewService.createCrew(reqDto3, image3, memberId3);

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew(new SearchParamDto("클로피", "Running"));

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result.size()).isEqualTo(1);
				softAssertions.assertThat(result).extracting("crewId")
					.contains(crewId3)
					.doesNotContain(crewId1, crewId2);
				softAssertions.assertThat(result).extracting("crewName")
					.allMatch(crewName -> String.valueOf(crewName).contains("클로피"));
				softAssertions.assertThat(result).extracting("category")
					.allMatch(category -> category.equals(Category.RUNNING));
			}
		);

	}

	@DisplayName("crewId를 통해 크루 정보를 조회할 수 있다.")
	@Test
	void findCrew() {
		//given
		Long memberId1 = saveMember1();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.AUTO,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		Long crewId = crewService.createCrew(reqDto, image, memberId1);

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
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");

		//when
		ApplyCrewResDto result = crewService.applyCrew(applyReqDto, crewId, memberId2);

		//then
		assertSoftly(
			softAssertions -> {
				softAssertions.assertThat(result)
					.extracting("crewId", "memberId")
					.containsExactly(crewId, memberId2);
				softAssertions.assertThat(result.isRequest()).isTrue();
			}
		);

	}

	@DisplayName("크루장은 크루에 신청한 사용자 목록을 확인할 수 있다.")
	@Test
	void getCrewApplicationList() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		//when
		ApprovalMemberResDto result = crewService.getApplyCrewList(crewId, memberId1);

		//then
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.approvalMember().size()).isEqualTo(1);
			softAssertions.assertThat(result.approvalMember().getFirst())
				.extracting("memberId", "nickname", "introduction", "gender", "age")
				.containsExactly(memberId2, "크루", "크루 가입 신청서", Gender.FEMALE, 20);
		});

	}

	@DisplayName("크루에 신청한 인원이 없는 경우 빈 리스트를 반환한다.")
	@Test
	void getCrewApplicationEmptyList() {
		//given
		Long memberId1 = saveMember1();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		//when
		ApprovalMemberResDto result = crewService.getApplyCrewList(crewId, memberId1);

		//then
		assertThat(result.approvalMember()).isEmpty();

	}

	@DisplayName("크루장은 크루 신청인원을 수락할 수 있다.")
	@Test
	void proceedApplicationCrew() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(memberId2, true);

		//when
		//then
		assertDoesNotThrow(() -> crewService.proceedApplyCrew(proceedApplyReqDto, crewId, memberId1));
	}

	@DisplayName("크루장은 크루 신청인원을 거절할 수 있다.")
	@Test
	void rejectApplicationCrew() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(memberId2, false);

		//when
		//then
		assertDoesNotThrow(() -> crewService.proceedApplyCrew(proceedApplyReqDto, crewId, memberId1));
	}

	@DisplayName("크루장은 크루원을 강퇴할 수 있다.")
	@Test
	void removeCrew() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(memberId2, true);
		crewService.proceedApplyCrew(proceedApplyReqDto, crewId, memberId1);

		//when
		//then
		assertDoesNotThrow(() -> crewService.removeCrewMember(crewId, memberId2, memberId1));
	}

	@DisplayName("크루원은 크루를 탈퇴할 수 있다.")
	@Test
	void leaveCrew() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(memberId2, true);
		crewService.proceedApplyCrew(proceedApplyReqDto, crewId, memberId1);

		//when
		//then
		assertDoesNotThrow(() -> crewService.leaveCrew(crewId, memberId2));
	}

	@DisplayName("크루장은 크루원이 있는 경우 탈퇴할 수 없다.")
	@Test
	void couldNotLeaveCrewLeader() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		ProceedApplyReqDto proceedApplyReqDto = new ProceedApplyReqDto(memberId2, true);
		crewService.proceedApplyCrew(proceedApplyReqDto, crewId, memberId1);

		//when
		//then
		assertThatThrownBy(() -> crewService.leaveCrew(crewId, memberId1))
			.isInstanceOf(ConflictException.class);
	}

	@DisplayName("크루장은 크루에 혼자 남은 경우 탈퇴할 수 있다.")
	@Test
	void leaveCrewLeader() {
		//given
		Long memberId1 = saveMember1();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		//when
		//then
		assertDoesNotThrow(() -> crewService.leaveCrew(crewId, memberId1));
	}

	@DisplayName("크루 신청자는 크루 신청 취소가 가능")
	@Test
	void cancelApplication() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		ApplyCrewReqDto applyReqDto = new ApplyCrewReqDto("크루 가입 신청서");
		crewService.applyCrew(applyReqDto, crewId, memberId2);

		//when
		ApplyCrewResDto result = crewService.cancelCrewApplication(crewId, memberId2);

		//then
		assertThat(result.isRequest()).isFalse();
	}

	@DisplayName("크루장은 크루 정보를 수정할 수 있다.")
	@Test
	void updateCrewInfo() {
		//given
		Long memberId1 = saveMember1();
		Long memberId2 = saveMember2();

		CreateCrewReqDto reqDto = new CreateCrewReqDto("크루명", Category.RUNNING, List.of("태그"), ApprovalType.MANUAL,
			"크루 소개", new RuleDto(3, 10));
		MockMultipartFile image = new MockMultipartFile("imgFile", "크루로고.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());
		Long crewId = crewService.createCrew(reqDto, image, memberId1);

		UpdateCrewReqDto updateCrewReqDto = new UpdateCrewReqDto(List.of("새로운 태그"), ApprovalType.AUTO, "새로운 크루 소개",
			new RuleDto(1, 2));
		MockMultipartFile updateImage = new MockMultipartFile("imgFile", "크루로고수정.png", MediaType.IMAGE_PNG_VALUE,
			"크루로고수정.png".getBytes());

		//when
		crewService.updateCrew(updateCrewReqDto, updateImage, crewId, memberId1);

		//then
		FindCrewResDto resDto = crewService.findCrew(crewId);
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(resDto)
				.extracting("approvalType", "introduction")
				.containsExactly(ApprovalType.AUTO, "새로운 크루 소개");
			softAssertions.assertThat(resDto.tags())
				.contains("새로운 태그");
			softAssertions.assertThat(resDto.rule())
				.isEqualTo(new RuleDto(1, 2));
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

	private Long saveMember2() {
		Member member2 = Member.builder()
			.nickname("크루")
			.profileImg("https://test.com")
			.gender(Gender.FEMALE)
			.age(20)
			.weight(60)
			.build();
		return memberRepository.save(member2).getId();
	}
}
