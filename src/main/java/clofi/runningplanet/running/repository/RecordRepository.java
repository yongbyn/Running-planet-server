package clofi.runningplanet.running.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.running.domain.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {

	Optional<Record> findByIdAndMemberAndEndTimeIsNotNull(Long id, Member member);

	Optional<Record> findOneByMemberAndEndTimeIsNull(Member member);

	List<Record> findAllByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);

	List<Record> findAllByMemberAndCreatedAtBetweenAndEndTimeIsNotNull(Member member, LocalDateTime startDateTime,
		LocalDateTime endDateTime);

	@Query("SELECT r FROM Record r WHERE r.member = :member AND r.createdAt >= :startOfToday AND r.createdAt < :startOfTomorrow")
	List<Record> findRunningRecordsByMemberAndDateRange(@Param("member") Member member,
		@Param("startOfToday") LocalDateTime startOfToday, @Param("startOfTomorrow") LocalDateTime startOfTomorrow);

	@Query("SELECT r FROM Record r WHERE r.member IN :members AND r.createdAt >= :startOfToday AND r.createdAt < :startOfTomorrow")
	List<Record> findRunningRecordsByMembersAndDateRange(@Param("members") List<Member> members,
		@Param("startOfToday") LocalDateTime startOfToday, @Param("startOfTomorrow") LocalDateTime startOfTomorrow);

}
