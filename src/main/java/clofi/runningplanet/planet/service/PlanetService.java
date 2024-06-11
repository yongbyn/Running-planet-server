package clofi.runningplanet.planet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.planet.domain.MemberPlanet;
import clofi.runningplanet.planet.domain.Planet;
import clofi.runningplanet.planet.dto.response.PlanetResponse;
import clofi.runningplanet.planet.repository.MemberPlanetRepository;
import clofi.runningplanet.planet.repository.PlanetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class PlanetService {

	private final PlanetRepository planetRepository;
	private final MemberPlanetRepository memberPlanetRepository;
	private final MemberRepository memberRepository;

	private final Random random = new Random();

	public List<PlanetResponse> getPlanetList(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));
		List<Planet> planetList = planetRepository.findAll();
		calculateMemberPlanetSize(planetList, member);
		List<MemberPlanet> memberPlanetList = memberPlanetRepository.findByMemberId(member);
		List<PlanetResponse> planetResponseList = new ArrayList<>();
		String planetStage = "";

		if (memberPlanetList.size() == 1) {
			if (member.getExp() >= 0 && member.getExp() < 2) {
				planetStage = memberPlanetList.get(0).getPlanetId().getFirstPlanet();
			} else if (member.getExp() >= 2 && member.getExp() < 4) {
				planetStage = memberPlanetList.get(0).getPlanetId().getSecondPlanet();
			} else if (member.getExp() >= 4 && member.getExp() < 6) {
				planetStage = memberPlanetList.get(0).getPlanetId().getThirdPlanet();
			} else if (member.getExp() >= 6 && member.getExp() < 8) {
				planetStage = memberPlanetList.get(0).getPlanetId().getFourthPlanet();
			} else {
				planetStage = memberPlanetList.get(0).getPlanetId().getFifthPlanet();
			}
			planetResponseList.add(new PlanetResponse(memberPlanetList.getFirst().getMemberPlanetId(),
				memberPlanetList.getFirst().getMemberPlanetName(), planetStage, 10, member.getExp()));

		} else {
			for (int i = 0; i < memberPlanetList.size(); i++) {
				if (i == 0) {
					planetResponseList.add(
						createPlanetResponse(memberPlanetList.get(i), planetStage, 10, 10));
				} else {
					int distance = (i == memberPlanetList.size() - 1)
						? member.getExp() - 10 - (50 * (memberPlanetList.size() - 2))
						: 50;
					planetStage = getPlanetStage(distance, planetStage, memberPlanetList);
					planetResponseList.add(createPlanetResponse(memberPlanetList.get(i), planetStage, 50, distance));
				}
			}
		}
		return planetResponseList;
	}

	private void calculateMemberPlanetSize(List<Planet> planetList, Member member) {
		List<MemberPlanet> memberPlanetList = memberPlanetRepository.findByMemberId(member);
		//	행성이 없어서 처음 만들 때
		if (memberPlanetList.isEmpty()) {
			createMemberPlanet(planetList, member);
		}
		//첫 행성 10km 조건을 달성하고 50km 요구 거리 행성 만들 때
		else if (memberPlanetList.size() == 1 && member.getExp() - 10 > 0) {
			createMemberPlanet(planetList, member);
		}
		//행성이 2개 이상 있고 새로운 행성을 만들어야 할 때
		else if (
			memberPlanetList.size() > 1 &&
				memberPlanetList.size() != (member.getExp() - 10) / 50 &&
				(member.getExp() - 10) - (50 * (memberPlanetList.size() - 1)) > 0) {
			createMemberPlanet(planetList, member);
		}
	}

	private void createMemberPlanet(List<Planet> planetList, Member member) {
		int randomNum = random.nextInt(planetList.size());
		Planet planet = planetList.get(randomNum);
		memberPlanetRepository.save(new MemberPlanet(
			member, planet, planet.getPlanetDefaultName()
		));
	}

	private static String getPlanetStage(int distance, String planetStage, List<MemberPlanet> memberPlanetList) {
		if (distance >= 0 && distance < 10) {
			planetStage = memberPlanetList.get(0).getPlanetId().getFirstPlanet();
		} else if (distance >= 10 && distance < 20) {
			planetStage = memberPlanetList.get(0).getPlanetId().getSecondPlanet();
		} else if (distance >= 20 && distance < 30) {
			planetStage = memberPlanetList.get(0).getPlanetId().getThirdPlanet();
		} else if (distance >= 30 && distance < 40) {
			planetStage = memberPlanetList.get(0).getPlanetId().getFourthPlanet();
		} else if (distance >= 40 && distance < 50) {
			planetStage = memberPlanetList.get(0).getPlanetId().getFifthPlanet();
		}
		return planetStage;
	}

	private PlanetResponse createPlanetResponse(MemberPlanet memberPlanet, String planetStage, int distance,
		int score) {
		return new PlanetResponse(
			memberPlanet.getMemberPlanetId(),
			memberPlanet.getMemberPlanetName(),
			planetStage,
			distance,
			score
		);
	}

}
