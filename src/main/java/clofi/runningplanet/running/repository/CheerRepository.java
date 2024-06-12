package clofi.runningplanet.running.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.running.domain.Cheer;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
	List<Cheer> findAllByFromMemberAndToMemberAndCreatedAtIsBetween(Member fromMember, Member toMember,
		LocalDateTime start, LocalDateTime end);
}
