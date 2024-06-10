package clofi.runningplanet.planet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.planet.domain.MemberPlanet;

public interface MemberPlanetRepository extends JpaRepository<MemberPlanet, Long> {
	List<MemberPlanet> findByMemberId(Member member);
}
