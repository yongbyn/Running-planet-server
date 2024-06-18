package clofi.runningplanet.rank.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
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
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.planet.domain.MemberPlanet;
import clofi.runningplanet.planet.domain.Planet;
import clofi.runningplanet.planet.repository.MemberPlanetRepository;
import clofi.runningplanet.planet.repository.PlanetRepository;
import clofi.runningplanet.rank.dto.CrewRankResponse;
import clofi.runningplanet.rank.dto.PersonalRankResponse;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.repository.RecordRepository;

@SpringBootTest
class RankServiceTest {

	@Autowired
	private CrewRepository crewRepository;
	@Autowired
	private RankService rankService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberPlanetRepository memberPlanetRepository;
	@Autowired
	private RecordRepository recordRepository;
	@Autowired
	private PlanetRepository planetRepository;

	@AfterEach
	void tearDown() {
		crewRepository.deleteAllInBatch();
		memberPlanetRepository.deleteAllInBatch();
		recordRepository.deleteAllInBatch();
		planetRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
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

	@DisplayName("전체 기간에서 거리로 개인 랭킹 조회를 할 수 있다.")
	@Test
	void getPersonalRank() {
		//given
		Member member = memberRepository.save(new Member(null, "1등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));
		Member secondMember = memberRepository.save(
			new Member(null, "2등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));

		Planet planet = planetRepository.save(new Planet("1", "2", "3", "4", "5", "기본행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "첫번째행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "두번째 행성"));
		memberPlanetRepository.save(new MemberPlanet(secondMember, planet, "2등 행성"));
		recordRepository.save(createRecord(member, 10, 10.0, 10, 10, true));
		recordRepository.save(createRecord(member, 10, 20.0, 10, 10, true));
		recordRepository.save(createRecord(secondMember, 10, 10.0, 10, 10, true));
		//when
		List<PersonalRankResponse> personalRankList = rankService.getPersonalRankList("DISTANCE", "TOTAL",
			LocalDate.now());
		//then
		assertThat(personalRankList.getFirst().getNickname()).isEqualTo("1등");
		assertThat(personalRankList.getFirst().getPlanetCnt()).isEqualTo(2);
		assertThat(personalRankList.getFirst().getDistance()).isEqualTo(40);
	}

	@DisplayName("전체 기간의 행성의 수를 개인 랭킹 조회를 할 수 있다.")
	@Test
	void getPersonalRankWeek() {
		//given
		Member member = memberRepository.save(new Member(null, "1등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));
		Member secondMember = memberRepository.save(
			new Member(null, "2등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));

		Planet planet = planetRepository.save(new Planet("1", "2", "3", "4", "5", "기본행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "첫번째행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "두번째 행성"));
		memberPlanetRepository.save(new MemberPlanet(secondMember, planet, "2등 행성"));
		recordRepository.save(createRecord(member, 10, 10.0, 10, 10, true));
		recordRepository.save(createRecord(member, 10, 20.0, 10, 10, true));
		recordRepository.save(createRecord(secondMember, 10, 10.0, 10, 10, true));
		//when
		List<PersonalRankResponse> personalRankList = rankService.getPersonalRankList("PLANET", "TOTAL",
			LocalDate.now());
		//then
		assertThat(personalRankList.getFirst().getNickname()).isEqualTo("1등");
		assertThat(personalRankList.getFirst().getPlanetCnt()).isEqualTo(2);
		assertThat(personalRankList.getFirst().getDistance()).isEqualTo(40);
	}

	@DisplayName("한 주의 행성읠 수로 개인 랭킹을 조회 할 수 있다.")
	@Test
	void getPersonalRankByWeekPlanet() {
		//given
		Member member = memberRepository.save(new Member(null, "1등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));
		Member secondMember = memberRepository.save(
			new Member(null, "2등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));

		Planet planet = planetRepository.save(new Planet("1", "2", "3", "4", "5", "기본행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "첫번째행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "두번째 행성"));
		memberPlanetRepository.save(new MemberPlanet(secondMember, planet, "2등 행성"));
		recordRepository.save(createRecord(member, 10, 10.0, 10, 10, true));
		recordRepository.save(createRecord(member, 10, 20.0, 10, 10, true));
		recordRepository.save(createRecord(secondMember, 10, 10.0, 10, 10, true));
		//when
		List<PersonalRankResponse> personalRankList = rankService.getPersonalRankList("PLANET", "WEEK",
			LocalDate.now());
		//then
		assertThat(personalRankList.getFirst().getNickname()).isEqualTo("1등");
		assertThat(personalRankList.getFirst().getPlanetCnt()).isEqualTo(2);

	}

	@DisplayName("한 주의 운동 거리 주간 조회를 할 수 있다.")
	@Test
	void getPersonalRankByWeekDistance() {
		//given
		Member member = memberRepository.save(new Member(null, "1등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));
		Member secondMember = memberRepository.save(
			new Member(null, "2등", Gender.FEMALE, 10, 40, "테스트", 10, 10, 10, 40));

		Planet planet = planetRepository.save(new Planet("1", "2", "3", "4", "5", "기본행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "첫번째행성"));
		memberPlanetRepository.save(new MemberPlanet(member, planet, "두번째 행성"));
		memberPlanetRepository.save(new MemberPlanet(secondMember, planet, "2등 행성"));
		recordRepository.save(createRecord(member, 10, 10.0, 10, 10, true));
		recordRepository.save(createRecord(member, 10, 20.0, 10, 10, true));
		recordRepository.save(createRecord(secondMember, 10, 10.0, 10, 10, true));
		//when
		List<PersonalRankResponse> personalRankList = rankService.getPersonalRankList("DISTANCE", "WEEK",
			LocalDate.now());
		//then
		assertThat(personalRankList.getFirst().getNickname()).isEqualTo("1등");
		assertThat(personalRankList.getFirst().getPlanetCnt()).isEqualTo(2);
		assertThat(personalRankList.getFirst().getDistance()).isEqualTo(30);
	}

	private Record createRecord(Member member, int avgPace, double runDistance, int runTime, int calories,
		boolean isEnd) {
		return Record.builder()
			.member(member)
			.avgPace(avgPace)
			.runDistance(runDistance)
			.runTime(runTime)
			.calories(calories)
			.isEnd(isEnd)
			.build();
	}
}