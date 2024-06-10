package clofi.runningplanet.mission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.mission.domain.CrewMission;

public interface CrewMissionRepository extends JpaRepository<CrewMission, Long> {

	List<CrewMission> findAllByCrewIdAndMemberId(Long crewId, Long memberId);
}
