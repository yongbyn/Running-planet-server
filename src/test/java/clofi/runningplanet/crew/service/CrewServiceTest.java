package clofi.runningplanet.crew.service;

import static clofi.runningplanet.crew.domain.ApprovalType.*;
import static clofi.runningplanet.crew.domain.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.Tag;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
class CrewServiceTest {

	@Mock
	private CrewRepository crewRepository;

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private CrewService crewService;

	@DisplayName("크루 생성 성공")
	@Test
	void successCreateCrew() {
		//given
		final RuleDto rule = new RuleDto(
			5,
			100
		);

		//todo 인증 기능 구현 완료 후 테스트 변경

		CreateCrewReqDto reqDto = new CreateCrewReqDto(
			"구름 크루",
			5,
			50,
			RUNNING,
			List.of("성실"),
			AUTO,
			"구름 크루는 성실한 크루",
			rule
		);

		given(crewRepository.save(any()))
			.willReturn(
				new Crew(1L, null, null, 0,
					0, null, null, null,
					0, 0, 0, 0));
		given(tagRepository.saveAll(anyList()))
			.willReturn(null);

		//when
		Long result = crewService.createCrew(reqDto);

		//then
		assertThat(result).isEqualTo(1L);

	}

	@DisplayName("크루 목록 조회 성공")
	@Test
	void successFindAllCrew() {
		//given
		given(crewRepository.findAll())
			.willReturn(List.of(
				new Crew(1L, 1L, "구름 크루", 10, 50,
					RUNNING, AUTO, "구름 크루는 성실한 크루", 5, 100,
					0, 0),
				new Crew(2L, 2L, "클로피 크루", 8, 90,
					RUNNING, MANUAL, "클로피 크루는 최고의 크루", 7, 500,
					1000, 3000)));

		given(tagRepository.findAllByCrewId(anyLong()))
			.willReturn(List.of(
				new Tag(1L, null, "성실")
			))
			.willReturn(List.of(
				new Tag(2L, null, "최고")
			));


		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew();

		//then
		final FindAllCrewResDto firstFindAllCrewResDto = FindAllCrewResDto.of(new Crew(1L, 1L, "구름 크루", 10, 50,
			RUNNING, AUTO, "구름 크루는 성실한 크루", 5, 100,
			0, 0), List.of("성실"), new CrewLeaderDto(1L, "임시 닉네임"));

		final FindAllCrewResDto secondFindAllCrewResDto = FindAllCrewResDto.of(new Crew(2L, 2L, "클로피 크루", 8, 90,
			RUNNING, MANUAL, "클로피 크루는 최고의 크루", 7, 500,
			1000, 3000), List.of("최고"), new CrewLeaderDto(2L, "임시 닉네임"));

		final List<FindAllCrewResDto> expect = List.of(firstFindAllCrewResDto, secondFindAllCrewResDto);

		assertThat(result).isEqualTo(expect);
	}

	@DisplayName("아무 크루도 없을 시 빈 리스트 반환")
	@Test
	void successEmptyCrew() {
		//given
		given(crewRepository.findAll())
			.willReturn(List.of());

		//when
		List<FindAllCrewResDto> result = crewService.findAllCrew();

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("크루 상세 조회 성공")
	@Test
	void successFindCrew() {
		//given
		given(crewRepository.findById(anyLong()))
			.willReturn(
				Optional.of(new Crew(1L, 1L, "구름 크루", 10, 50,
					RUNNING, AUTO, "구름 크루는 성실한 크루", 5, 100,
					0, 0))
				);

		given(tagRepository.findAllByCrewId(anyLong()))
			.willReturn(List.of(
				new Tag(1L, null, "성실")
			));

		//when
		FindCrewResDto result = crewService.findCrew(1L);

		//then
		final FindCrewResDto findCrewResDto = FindCrewResDto.of(new Crew(1L, 1L, "구름 크루", 10, 50,
			RUNNING, AUTO, "구름 크루는 성실한 크루", 5, 100,
			0, 0), new CrewLeaderDto(1L, "임시 닉네임"), List.of("성실"));

		assertThat(result).isEqualTo(findCrewResDto);
	}
}
