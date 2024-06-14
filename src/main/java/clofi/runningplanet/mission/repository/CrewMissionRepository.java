package clofi.runningplanet.mission.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import clofi.runningplanet.mission.domain.CrewMission;

public interface CrewMissionRepository extends JpaRepository<CrewMission, Long> {

	@Query("select cm from CrewMission cm where cm.crew.id = :crewId and cm.member.id = :memberId and cm.createdAt between :startOfDay and :endOfDay")
	List<CrewMission> findAllByCrewIdAndMemberIdAndToday(
		@Param("crewId") Long crewId,
		@Param("memberId") Long memberId,
		@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay);

	@Query("SELECT cm FROM CrewMission cm WHERE cm.crew.id = :crewId AND cm.createdAt BETWEEN :startOfWeek AND :endOfWeek")
	List<CrewMission> findAllByCrewIdAndWeek(@Param("crewId") Long crewId,
		@Param("startOfWeek") LocalDateTime startOfWeek,
		@Param("endOfWeek") LocalDateTime endOfWeek);

	@Query("select cm from CrewMission cm where cm.crew.id = :crewId and cm.member.id in :memberIds")
	List<CrewMission> findByCrewIdAndMemberIds(@Param("crewId") Long crewId, @Param("memberIds") List<Long> memberIds);
}
