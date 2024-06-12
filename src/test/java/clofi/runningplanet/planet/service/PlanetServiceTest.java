package clofi.runningplanet.planet.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.planet.domain.MemberPlanet;
import clofi.runningplanet.planet.domain.Planet;
import clofi.runningplanet.planet.dto.request.UpdatePlanetNameRequest;
import clofi.runningplanet.planet.dto.response.PlanetResponse;
import clofi.runningplanet.planet.repository.MemberPlanetRepository;
import clofi.runningplanet.planet.repository.PlanetRepository;

@SpringBootTest
class PlanetServiceTest {

	@Autowired
	private PlanetService planetService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PlanetRepository planetRepository;
	@Autowired
	private MemberPlanetRepository memberPlanetRepository;

	@AfterEach
	void tearDown() {
		memberPlanetRepository.deleteAllInBatch();
		planetRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("행성이 존재하지 않을 때 행성을 조회할 수 있다.")
	@Test
	void getPlanetWhen0() {
		//given
		Member member = new Member(null, "테스트", Gender.MALE, 10, 100, "테스트", 5, 10, 10, 10);
		memberRepository.save(member);
		Planet planet = new Planet("이미지1", "이미지2", "이미지3", "이미지4", "이미지5", "테스트 이미지");
		planetRepository.save(planet);
		//when
		List<PlanetResponse> planetList = planetService.getPlanetList(member.getId());
		//then
		assertThat(planetList.size()).isEqualTo(1);
		assertThat(planetList.getFirst().planetName()).isEqualTo("테스트 이미지");
		assertThat(planetList.getFirst().planetImage()).isEqualTo("이미지3");
	}

	@DisplayName("행성이 1개 있을 때 새롭게 하나를 만들 수 있다.")
	@Test
	void createPlanet() {
		//given
		Member member = new Member(null, "테스트", Gender.MALE, 10, 100, "테스트", 45, 10, 10, 10);
		memberRepository.save(member);
		Planet planet = new Planet("이미지1", "이미지2", "이미지3", "이미지4", "이미지5", "두번째 이미지");
		planetRepository.save(planet);
		MemberPlanet memberPlanet = new MemberPlanet(member, planet, "테스트 이미지");
		memberPlanetRepository.save(memberPlanet);
		//when
		List<PlanetResponse> planetList = planetService.getPlanetList(member.getId());
		//then
		assertThat(planetList.size()).isEqualTo(2);
		assertThat(planetList.getLast().planetName()).isEqualTo("두번째 이미지");
		assertThat(planetList.getLast().planetImage()).isEqualTo("이미지4");
		assertThat(planetList.getLast().demandDistance()).isEqualTo(50);
		assertThat(planetList.getLast().distance()).isEqualTo(35);
	}

	@DisplayName("경험치가 넘지 않았을 때 행성 조회 시")
	@Test
	void getPlanetNotOverExp() {
		//given
		Member member = new Member(null, "테스트", Gender.MALE, 10, 100, "테스트", 45, 10, 10, 10);
		memberRepository.save(member);
		Planet planet = new Planet("이미지1", "이미지2", "이미지3", "이미지4", "이미지5", "두번째 이미지");
		planetRepository.save(planet);
		MemberPlanet memberPlanet = new MemberPlanet(member, planet, "테스트 이미지");
		memberPlanetRepository.save(memberPlanet);
		MemberPlanet memberPlanet2 = new MemberPlanet(member, planet, "두번째 행성");
		memberPlanetRepository.save(memberPlanet2);
		//when
		List<PlanetResponse> planetList = planetService.getPlanetList(member.getId());
		//then
		assertThat(planetList.size()).isEqualTo(2);
		assertThat(planetList.getLast().planetName()).isEqualTo("두번째 행성");
		assertThat(planetList.getLast().planetImage()).isEqualTo("이미지4");
		assertThat(planetList.getLast().demandDistance()).isEqualTo(50);
		assertThat(planetList.getLast().distance()).isEqualTo(35);

	}

	@DisplayName("사용자는 행성의 이름을 수정할 수 있다.")
	@Test
	void updatePlanetName() {
		//given
		Member member = new Member(null, "테스트", Gender.MALE, 10, 100, "테스트", 45, 10, 10, 10);
		memberRepository.save(member);
		Planet planet = new Planet("이미지1", "이미지2", "이미지3", "이미지4", "이미지5", "두번째 이미지");
		planetRepository.save(planet);
		MemberPlanet memberPlanet = new MemberPlanet(member, planet, "테스트 이미지");
		memberPlanetRepository.save(memberPlanet);
		UpdatePlanetNameRequest updatePlanetNameRequest = new UpdatePlanetNameRequest("수정된 행성 이름");
		//when
		Long planetId = planetService.updatePlanet(memberPlanet.getMemberPlanetId(),
			updatePlanetNameRequest,
			member.getId());
		MemberPlanet updatedPlanet = memberPlanetRepository.findById(planetId)
			.orElseThrow(() -> new IllegalArgumentException("행성이 없습니다."));
		//then
		assertThat(updatedPlanet.getMemberPlanetName()).isEqualTo("수정된 행성 이름");

	}
}