package clofi.runningplanet.crew.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.member.domain.Member;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
	Optional<CrewMember> findByMemberId(Long id);

	boolean existsByMemberId(Long memberId);

	List<CrewMember> findByCrewId(Long crewId);

	int countByCrewId(Long id);

	Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

	boolean existsByCrewIdAndMemberId(Long crewId, Long memberId);

	@Query("SELECT cm.member FROM CrewMember cm WHERE cm.crew.id = :crewId")
	List<Member> findMembersByCrewId(@Param("crewId") Long crewId);
}
