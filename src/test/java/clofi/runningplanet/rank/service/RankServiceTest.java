package clofi.runningplanet.rank.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.rank.dto.CrewRankResponse;

@SpringBootTest
class RankServiceTest {

	@Autowired
	private CrewRepository crewRepository;
	@Autowired
	private RankService rankService;

	@AfterEach
	void tearDown() {
		crewRepository.deleteAllInBatch();
	}

	@DisplayName("크루 랭킹을 거리로 조회할 수 있다")
	@Test
	void getCrewRankByDistance() {
		//given
		Crew firstCrew = new Crew(null, 1L, "1등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "1등 크루", 10, 10, 100, 100,
			100,
			10);
		crewRepository.save(firstCrew);
		Crew secondCrew = new Crew(null, 2L, "2등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "2등 크루", 10, 10, 100, 90,
			100, 10);
		crewRepository.save(secondCrew);
		Crew thirdCrew = new Crew(null, 3L, "3등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "3등 크루", 10, 10, 100, 80,
			100,
			10);
		crewRepository.save(thirdCrew);
		//when
		List<CrewRankResponse> crewRankList = rankService.getCrewRankList("DISTANCE", "TOTAL");
		//then
		assertThat(crewRankList.get(0).getCrewName()).isEqualTo("1등 크루");
		assertThat(crewRankList.get(1).getCrewName()).isEqualTo("2등 크루");
		assertThat(crewRankList.getLast().getCrewName()).isEqualTo("3등 크루");
	}

	@DisplayName("크루 랭킹을 레벨로 조회할 수 있다")
	@Test
	void getCrewRankByLevel() {
		//given
		Crew firstCrew = new Crew(null, 1L, "1등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "1등 크루", 10, 10, 100, 100,
			100, 10);
		crewRepository.save(firstCrew);
		Crew secondCrew = new Crew(null, 2L, "2등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "2등 크루", 10, 10, 100, 90,
			100, 9);
		crewRepository.save(secondCrew);
		Crew thirdCrew = new Crew(null, 3L, "3등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "3등 크루", 10, 10, 100, 80,
			100, 8);
		crewRepository.save(thirdCrew);
		//when
		List<CrewRankResponse> crewRankList = rankService.getCrewRankList("LEVEL", "TOTAL");

		//then
		assertThat(crewRankList.get(0).getCrewName()).isEqualTo("1등 크루");
		assertThat(crewRankList.get(1).getCrewName()).isEqualTo("2등 크루");
		assertThat(crewRankList.getLast().getCrewName()).isEqualTo("3등 크루");
	}

	@DisplayName("크루 랭킹을 주간 거리로 조회할 수 있다")
	@Test
	void getCrewRankByWeeklyDistance() {
		//given
		Crew firstCrew = new Crew(null, 1L, "1등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "1등 크루", 10, 10, 100, 100,
			100, 10);
		crewRepository.save(firstCrew);
		Crew secondCrew = new Crew(null, 2L, "2등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "2등 크루", 10, 10, 90, 90,
			100, 9);
		crewRepository.save(secondCrew);
		Crew thirdCrew = new Crew(null, 3L, "3등 크루", 10, Category.RUNNING, ApprovalType.AUTO, "3등 크루", 10, 10, 80, 80,
			100, 8);
		crewRepository.save(thirdCrew);
		//when
		List<CrewRankResponse> crewRankList = rankService.getCrewRankList("DISTANCE", "WEEK");
		//then
		assertThat(crewRankList.get(0).getCrewName()).isEqualTo("1등 크루");
		assertThat(crewRankList.get(1).getCrewName()).isEqualTo("2등 크루");
		assertThat(crewRankList.getLast().getCrewName()).isEqualTo("3등 크루");
	}
}