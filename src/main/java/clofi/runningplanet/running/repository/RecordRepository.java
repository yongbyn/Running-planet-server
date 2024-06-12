package clofi.runningplanet.running.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.running.domain.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {

	Optional<Record> findByIdAndMemberAndEndTimeIsNotNull(Long id, Member member);

	Optional<Record> findOneByMemberAndEndTimeIsNull(Member member);

	List<Record> findAllByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);

	List<Record> findAllByMemberAndCreatedAtBetweenAndEndTimeIsNotNull(Member member, LocalDateTime start,
		LocalDateTime end);

	List<Record> findAllByMemberInAndCreatedAtBetween(List<Member> members, LocalDateTime start, LocalDateTime end);

	List<Record> findAllByMember(Member member);

	List<Record> findAllByEndTimeIsNullAndCreatedAtBetweenAndMemberIn(LocalDateTime start, LocalDateTime end, List<Member> members);
}
