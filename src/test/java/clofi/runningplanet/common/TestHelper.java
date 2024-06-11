package clofi.runningplanet.common;

import static clofi.runningplanet.crew.domain.ApprovalType.*;
import static clofi.runningplanet.crew.domain.Category.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewImage;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.running.domain.Record;

public class TestHelper {

	public static RuleDto createRule() {
		return new RuleDto(5, 100);
	}

	public static CreateCrewReqDto getCreateCrewReqDto() {
		return new CreateCrewReqDto("구름 크루", RUNNING, List.of("성실"), MANUAL, "구름 크루는 성실한 크루", createRule()
		);
	}

	public static ApplyCrewReqDto getApplyCrewReqDto() {
		return new ApplyCrewReqDto("크루 신청글");
	}

	public static MockMultipartFile createImage() {
		return new MockMultipartFile("크루로고", "크루로고.png", MediaType.IMAGE_PNG_VALUE, "크루로고.png".getBytes());
	}

	public static Crew createCrew() {
		return new Crew(1L, 1L, "구름 크루", 10, RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100, 0, 0, 0, 1);
	}

	public static Crew createAutoCrew() {
		return new Crew(2L, 2L, "클로피 크루", 8, RUNNING, AUTO, "클로피 크루는 최고의 크루", 7, 500, 1000, 3000, 0, 1);
	}

	public static Member createLeader() {
		return new Member(1L, "크루장", Gender.MALE, 20, 70, "https://image-url.com", 0, 10, 30, 100);
	}

	public static Member createMember() {
		return new Member(2L, "사용자", Gender.FEMALE, 30, 80, "https://image-url.com", 0, 0, 0, 0);
	}

	public static List<CrewMission> crewMissionList() {
		return List.of(
			new CrewMission(1L, createLeader(), createCrew(), MissionType.DISTANCE, true),
			new CrewMission(2L, createLeader(), createCrew(), MissionType.DURATION, false)
		);
	}

	public static List<CrewMission> crewMissionListNotComplete() {
		return List.of(
			new CrewMission(1L, createLeader(), createCrew(), MissionType.DISTANCE, false),
			new CrewMission(2L, createLeader(), createCrew(), MissionType.DURATION, false)
		);
	}

	public static List<Record> createTodayRecordList() {
		return List.of(
			Record.builder()
				.member(createLeader())
				.runTime(1800)
				.runDistance(1000)
				.avgPace(100)
				.calories(100)
				.endTime(LocalDateTime.now())
				.build()
		);
	}

	public static CrewMission createDistanceCrewMission() {
		return new CrewMission(1L, createLeader(), createCrew(), MissionType.DISTANCE, false);
	}

	public static CrewMission createCompleteDistanceCrewMission() {
		return new CrewMission(1L, createLeader(), createCrew(), MissionType.DISTANCE, true);
	}

	public static CrewMission createDurationCrewMission() {
		return new CrewMission(1L, createLeader(), createCrew(), MissionType.DURATION, false);
	}

	public static CrewImage createCrewImage() {
		return new CrewImage(1L, "크루 로고", "https://test.com", createCrew());
	}

}
