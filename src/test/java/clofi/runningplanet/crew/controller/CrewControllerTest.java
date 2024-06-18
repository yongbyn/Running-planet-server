package clofi.runningplanet.crew.controller;

import static clofi.runningplanet.crew.domain.ApprovalType.*;
import static clofi.runningplanet.crew.domain.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
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
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import clofi.runningplanet.common.WithMockCustomMember;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.SearchParamDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.ProceedApplyReqDto;
import clofi.runningplanet.crew.dto.request.UpdateCrewReqDto;
import clofi.runningplanet.crew.dto.response.ApplyCrewResDto;
import clofi.runningplanet.crew.dto.response.ApprovalMemberResDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewMemberResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewWithMissionResDto;
import clofi.runningplanet.crew.dto.response.GetApplyCrewResDto;
import clofi.runningplanet.crew.service.CrewService;
import clofi.runningplanet.member.domain.Gender;

@WebMvcTest(CrewController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CrewControllerTest {

	@MockBean
	CrewService crewService;

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

	@DisplayName("크루 생성 성공")
	@WithMockCustomMember
	@Test
	void createCrew() throws Exception {
		//given
		final RuleDto rule = new RuleDto(
			5,
			100
		);

		CreateCrewReqDto reqDto = new CreateCrewReqDto(
			"크루명",
			RUNNING,
			List.of("성실"),
			AUTO,
			"크루를 소개하는 글",
			rule
		);

		MockMultipartFile imageFile = new MockMultipartFile("imgFile", "크루로고.png", IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		given(crewService.createCrew(any(CreateCrewReqDto.class), any(MockMultipartFile.class), anyLong()))
			.willReturn(1L);

		//when
		ResultActions resultAction = createCrew(reqDto, imageFile);

		//then
		resultAction
			.andExpect(status().isCreated())
			.andExpect(header().string(LOCATION, "/api/crew/1"));

	}

	@DisplayName("크루 목록 조회 성공")
	@Test
	void FindAllCrew() throws Exception {
		//given
		List<FindAllCrewResDto> expected = List.of(
			new FindAllCrewResDto(1L, "구름 크루", 1, 1,
				10, AUTO, "https://test.com", List.of("성실"), RUNNING,
				new RuleDto(3, 100),
				"구름 크루는 성실한 크루",
				new CrewLeaderDto(1L, "임시 닉네임")),
			new FindAllCrewResDto(2L, "클로피 크루", 1, 1,
				8, MANUAL, "https://test.com", List.of("최고"), RUNNING,
				new RuleDto(3, 100),
				"클로피 크루는 최고의 크루",
				new CrewLeaderDto(2L, "임시 닉네임"))
		);
		given(crewService.findAllCrew(any(SearchParamDto.class)))
			.willReturn(expected);

		//when
		ResultActions resultAction = findAllCrew();

		//then
		MvcResult mvcResult = resultAction
			.andExpect(status().isOk())
			.andReturn();

		List<FindAllCrewResDto> resDtoList = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(resDtoList).isEqualTo(expected);
	}

	@DisplayName("크루 상세 조회 성공")
	@Test
	void findCrew() throws Exception {
		//given
		Long crewId = 1L;
		FindCrewResDto expected = new FindCrewResDto(1L, 1, "구름 크루",
			new CrewLeaderDto(1L, "임시 닉네임"), 5, 10, AUTO,
			"구름 크루는 성실한 크루", "https://test.com", List.of("성실"), RUNNING,
			new RuleDto(5, 100), 0, false);

		given(crewService.findCrew(crewId))
			.willReturn(expected);

		//when
		ResultActions resultActions = findCrew(crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isOk())
			.andReturn();

		FindCrewResDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(resDto).isEqualTo(expected);
	}

	@DisplayName("크루 신청 성공")
	@WithMockCustomMember
	@Test
	void successApplyCrew() throws Exception {
		//given
		Long crewId = 1L;
		ApplyCrewReqDto reqDto = new ApplyCrewReqDto("크루 신청서");

		ApplyCrewResDto expected = new ApplyCrewResDto(crewId, 1L, true);

		given(crewService.applyCrew(any(ApplyCrewReqDto.class), anyLong(), anyLong()))
			.willReturn(expected);

		//when
		ResultActions resultActions = applyCrew(reqDto, crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isCreated())
			.andReturn();

		ApplyCrewResDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(expected).isEqualTo(resDto);
	}

	@DisplayName("크루 신청 목록 조회 성공")
	@WithMockCustomMember
	@Test
	void successGetApplyList() throws Exception {
		//given
		Long crewId = 1L;

		GetApplyCrewResDto getApplyCrewResDto1 = new GetApplyCrewResDto(2L, "닉네임1", "크루 신청글1", Gender.MALE, 30,
			"https://test.com");
		GetApplyCrewResDto getApplyCrewResDto2 = new GetApplyCrewResDto(3L, "닉네임2", "크루 신청글2", Gender.FEMALE, 15,
			"https://test.com");
		ApprovalMemberResDto expected = new ApprovalMemberResDto(
			List.of(getApplyCrewResDto1, getApplyCrewResDto2));

		given(crewService.getApplyCrewList(anyLong(), anyLong()))
			.willReturn(expected);

		//when
		ResultActions resultActions = getApplyCrewList(crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isOk())
			.andReturn();

		ApprovalMemberResDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(expected).isEqualTo(resDto);
	}

	@DisplayName("크루 가입 승인 성공")
	@WithMockCustomMember
	@Test
	void successApprove() throws Exception {
		//given
		ProceedApplyReqDto reqDto = new ProceedApplyReqDto(2L, true);
		Long crewId = 1L;

		doNothing()
			.when(crewService)
			.proceedApplyCrew(any(ProceedApplyReqDto.class), anyLong(), anyLong());

		//when
		ResultActions resultActions = proceedApplyCrew(reqDto, crewId);

		//then
		resultActions
			.andExpect(status().isOk())
			.andReturn();
	}

	@DisplayName("크루원 강퇴 성공")
	@WithMockCustomMember
	@Test
	void successRemove() throws Exception {
		//given
		Long crewId = 1L;
		Long memberId = 2L;

		doNothing()
			.when(crewService)
			.removeCrewMember(anyLong(), anyLong(), anyLong());

		//when
		ResultActions resultActions = removeCrew(crewId, memberId);

		//then
		resultActions
			.andExpect(status().isOk())
			.andReturn();
	}

	@DisplayName("크루 탈퇴 성공")
	@WithMockCustomMember
	@Test
	void successLeaveCrew() throws Exception {
		//given
		Long crewId = 1L;

		doNothing()
			.when(crewService)
			.leaveCrew(anyLong(), anyLong());

		//when
		ResultActions resultActions = leaveCrew(crewId);

		//then
		resultActions
			.andExpect(status().isOk())
			.andReturn();

	}

	@DisplayName("크루 신청 취소 성공")
	@WithMockCustomMember
	@Test
	void successCancelCrewApplication() throws Exception {
		//given
		Long crewId = 1L;

		ApplyCrewResDto expected = new ApplyCrewResDto(1L, 1L, false);

		given(crewService.cancelCrewApplication(anyLong(), anyLong()))
			.willReturn(expected);

		//when
		ResultActions resultActions = cancelCrewApplication(crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isOk())
			.andReturn();

		ApplyCrewResDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(expected).isEqualTo(resDto);
	}

	@DisplayName("크루 정보 수정 성공")
	@WithMockCustomMember
	@Test
	void successUpdateCrew() throws Exception {
		//given
		Long crewId = 1L;
		UpdateCrewReqDto reqDto = new UpdateCrewReqDto(List.of("수정1", "수정2"), AUTO, "크루 소개 수정",
			new RuleDto(3, 10));
		MockMultipartFile imageFile = new MockMultipartFile("imgFile", "크루로고.png", IMAGE_PNG_VALUE,
			"크루로고.png".getBytes());

		doNothing()
			.when(crewService)
			.updateCrew(any(UpdateCrewReqDto.class), any(MockMultipartFile.class), anyLong(), anyLong());

		//when
		ResultActions resultActions = updateCrew(reqDto, imageFile, crewId);

		//then
		resultActions
			.andExpect(status().isOk());

	}

	@DisplayName("크루 페이지를 조회할 수 있다.")
	@WithMockCustomMember
	@Test
	void successCrewPage() throws Exception {
		//given
		Long crewId = 1L;

		FindCrewWithMissionResDto expected = new FindCrewWithMissionResDto(1L, 1, "크루명", "크루 소개", 10, 10, List.of("태그"),
			RUNNING, new RuleDto(5, 999), 2, List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), true, "https://test.com");

		given(crewService.findCrewWithMission(anyLong(), anyLong()))
			.willReturn(expected);

		//when
		ResultActions resultActions = findCrewWithMission(crewId);

		//then
		MvcResult mvcResult = resultActions
			.andExpect(status().isOk())
			.andReturn();

		FindCrewWithMissionResDto resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(resDto).isEqualTo(expected);
	}

	@DisplayName("크루 명단 조회 성공")
	@WithMockCustomMember
	@Test
	void successFindCrewMemberList() throws Exception {
		//given
		Long crewId = 1L;

		FindCrewMemberResDto resDto1 = new FindCrewMemberResDto(1L, "크루장", 4, true);
		FindCrewMemberResDto resDto2 = new FindCrewMemberResDto(2L, "크루원", 2, false);
		List<FindCrewMemberResDto> resDtoList = List.of(resDto1, resDto2);

		given(crewService.findCrewMemberList(anyLong(), anyLong()))
			.willReturn(resDtoList);

		//when
		ResultActions resultActions = findCrewMemberList(crewId);

		//then
		MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		List<FindCrewMemberResDto> resDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);

		assertThat(resDto).isEqualTo(resDtoList);
	}

	private ResultActions createCrew(CreateCrewReqDto reqDto, MockMultipartFile imgFile) throws Exception {
		MockMultipartFile jsonFile = new MockMultipartFile("crewInfo", "", "application/json",
			objectMapper.writeValueAsBytes(reqDto));
		return mockMvc.perform(multipart("/api/crew")
			.file(imgFile)
			.file(jsonFile)
			.contentType(MULTIPART_FORM_DATA)
			.header(AUTHORIZATION, "Bearer accessToken")
			.content(objectMapper.writeValueAsString(reqDto)));
	}

	private ResultActions findAllCrew() throws Exception {
		return mockMvc.perform(get("/api/crew")
			.contentType(APPLICATION_JSON));
	}

	private ResultActions findCrew(Long crewId) throws Exception {
		return mockMvc.perform(get("/api/crew/{crewId}", crewId)
			.contentType(APPLICATION_JSON));
	}

	private ResultActions applyCrew(ApplyCrewReqDto reqDto, Long crewId) throws Exception {
		return mockMvc.perform(post("/api/crew/{crewId}", crewId)
			.contentType(APPLICATION_JSON)
			.header(AUTHORIZATION, "Bearer accessToken")
			.content(objectMapper.writeValueAsString(reqDto)));
	}

	private ResultActions getApplyCrewList(Long crewId) throws Exception {
		return mockMvc.perform(get("/api/crew/{crewId}/request", crewId)
			.contentType(APPLICATION_JSON));
	}

	private ResultActions proceedApplyCrew(ProceedApplyReqDto reqDto, Long crewId) throws Exception {
		return mockMvc.perform(post("/api/crew/{crewId}/request", crewId)
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(reqDto)));
	}

	private ResultActions removeCrew(Long crewId, Long memberId) throws Exception {
		return mockMvc.perform(delete("/api/crew/{crewId}/{memberId}", crewId, memberId));
	}

	private ResultActions leaveCrew(Long crewId) throws Exception {
		return mockMvc.perform(delete("/api/crew/{crewId}", crewId));
	}

	private ResultActions cancelCrewApplication(Long crewId) throws Exception {
		return mockMvc.perform(delete("/api/crew/{crewId}/request", crewId)
			.contentType(APPLICATION_JSON));
	}

	private ResultActions updateCrew(UpdateCrewReqDto reqDto, MockMultipartFile imgFile, Long crewId) throws Exception {
		MockMultipartFile jsonFile = new MockMultipartFile("modifyInfo", "", "application/json",
			objectMapper.writeValueAsBytes(reqDto));
		return mockMvc.perform(multipart(HttpMethod.PATCH, "/api/crew/{crewId}", crewId)
			.file(imgFile)
			.file(jsonFile)
			.contentType(MULTIPART_FORM_DATA)
			.header(AUTHORIZATION, "Bearer accessToken")
			.content(objectMapper.writeValueAsString(reqDto)));
	}

	private ResultActions findCrewWithMission(Long crewId) throws Exception {
		return mockMvc.perform(get("/api/crew/{crewId}/page", crewId)
			.contentType(APPLICATION_JSON));
	}

	private ResultActions findCrewMemberList(Long crewId) throws Exception {
		return mockMvc.perform(get("/api/crew/{crewId}/member", crewId)
			.contentType(APPLICATION_JSON));
	}
}
