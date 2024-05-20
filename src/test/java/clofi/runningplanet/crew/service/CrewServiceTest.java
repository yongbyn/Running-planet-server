package clofi.runningplanet.crew.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.RuleReqDto;
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
		final RuleReqDto rule = new RuleReqDto(
			5,
			100
		);

		//todo 인증 기능 구현 완료 후 테스트 변경

		CreateCrewReqDto reqDto = new CreateCrewReqDto(
			"구름 크루",
			5,
			50,
			Category.RUNNING,
			List.of("성실"),
			ApprovalType.AUTO,
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
}
