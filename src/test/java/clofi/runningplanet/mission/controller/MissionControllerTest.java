package clofi.runningplanet.mission.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import clofi.runningplanet.common.WithMockCustomMember;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.dto.response.GetCrewMissionResDto;
import clofi.runningplanet.mission.service.MissionService;

@WebMvcTest(MissionController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MissionControllerTest {

	@MockBean
	private MissionService missionService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
	}

	@DisplayName("크루 미션 목록 조회 성공")
	@WithMockCustomMember
	@Test
	void successGetCrewMission() throws Exception {
		//given
		Long crewId = 1L;

		List<GetCrewMissionResDto> getCrewMissionResDtos = List.of(
			new GetCrewMissionResDto(1L, MissionType.DISTANCE, 1, true),
			new GetCrewMissionResDto(2L, MissionType.DURATION, (double)(1800 / 3600), false)
		);

		CrewMissionListDto expected = new CrewMissionListDto(getCrewMissionResDtos);

		given(missionService.getCrewMission(anyLong(), anyLong()))
			.willReturn(expected);

		//when
		ResultActions resultActions = getCrewMissionList(crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isOk())
			.andReturn();

		CrewMissionListDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(expected).isEqualTo(resDto);
	}

	@DisplayName("크루 미션 성공")
	@WithMockCustomMember
	@Test
	void test() throws Exception {
		//given
		Long crewId = 1L;
		Long missionId = 1L;

		doNothing()
			.when(missionService)
			.successMission(anyLong(), anyLong(), anyLong());

		//when
		ResultActions resultActions = successCrewMission(crewId, missionId);

		//then
		resultActions
			.andExpect(status().isOk());

	}

	private ResultActions getCrewMissionList(Long crewId) throws Exception {
		return mockMvc.perform(get("/api/crew/{crewId}/mission", crewId)
			.contentType(APPLICATION_JSON));
	}

	private ResultActions successCrewMission(Long crewId, Long missionId) throws Exception {
		return mockMvc.perform(post("/api/crew/{crewId}/mission/{missionId}", crewId, missionId)
			.contentType(APPLICATION_JSON));
	}
}
