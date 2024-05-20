package clofi.runningplanet.crew.controller;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.RuleReqDto;
import clofi.runningplanet.crew.service.CrewService;

@WebMvcTest(CrewController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CrewControllerTest {

	@MockBean
	CrewService crewService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("주문 생성 성공")
	@Test
	void createCrew() throws Exception {
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

		given(crewService.createCrew(any(CreateCrewReqDto.class)))
			.willReturn(1L);

		//when
		ResultActions resultAction = createOrder(reqDto);

		//then
		resultAction
			.andExpect(status().isCreated())
			.andExpect(header().string(LOCATION, "/api/crew/1"));

	}

	private ResultActions createOrder(CreateCrewReqDto reqDto) throws Exception {
		return mockMvc.perform(post("/api/crew")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(reqDto)));
	}
}
