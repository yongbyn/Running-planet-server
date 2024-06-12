package clofi.runningplanet.mission.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import clofi.runningplanet.mission.domain.CrewMission;

public interface CrewMissionRepository extends JpaRepository<CrewMission, Long> {

	List<CrewMission> findAllByCrewIdAndMemberId(Long crewId, Long memberId);

	@Query("SELECT cm FROM CrewMission cm WHERE cm.crew.id = :crewId AND cm.createdAt >= :startOfWeek AND cm.createdAt <= :endOfWeek")
	List<CrewMission> findAllByCrewIdAndWeek(@Param("crewId") Long crewId,
		@Param("startOfWeek") LocalDateTime startOfWeek,
		@Param("endOfWeek") LocalDateTime endOfWeek
	);
}
