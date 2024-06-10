package clofi.runningplanet.mission.service;

import static clofi.runningplanet.common.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.dto.response.GetCrewMissionResDto;
import clofi.runningplanet.mission.repository.CrewMissionRepository;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.repository.RecordRepository;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

	@Mock
	private CrewMissionRepository crewMissionRepository;

	@Mock
	private CrewRepository crewRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CrewMemberRepository crewMemberRepository;

	@Mock
	private RecordRepository recordRepository;

	@InjectMocks
	private MissionService missionService;

	@DisplayName("크루 미션 목록 조회 성공")
	@Test
	void successGetAllCrewMission() {
		//given
		Long crewId = 1L;
		Long memberId = 1L;

		List<CrewMission> crewMissionList = crewMissionList();
		List<Record> todayRecordList = createTodayRecordList();

		given(crewRepository.existsById(anyLong()))
			.willReturn(true);
		given(memberRepository.existsById(anyLong()))
			.willReturn(true);
		given(crewMemberRepository.existsByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(true);
		given(crewMissionRepository.findAllByCrewIdAndMemberId(anyLong(), anyLong()))
			.willReturn(crewMissionList);
		given(recordRepository.findAllByMemberIdAndCreatedAtBetween(anyLong(), any(LocalDateTime.class), any(
			LocalDateTime.class)))
			.willReturn(todayRecordList);

		//when
		CrewMissionListDto result = missionService.getCrewMission(crewId, memberId);

		//then
		List<GetCrewMissionResDto> getCrewMissionResDtos = List.of(
			new GetCrewMissionResDto(1L, MissionType.DISTANCE, 1, true),
			new GetCrewMissionResDto(2L, MissionType.DURATION, (double)(1800 / 3600), false)
		);

		CrewMissionListDto expected = new CrewMissionListDto(getCrewMissionResDtos);

		assertThat(result).isEqualTo(expected);
	}
}
