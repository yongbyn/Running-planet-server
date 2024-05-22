package clofi.runningplanet.running.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.running.domain.Record;

// TODO: 조건에 Member 추가하기
public interface RecordRepository extends JpaRepository<Record, Long> {

	Optional<Record> findByIdAndEndTimeIsNotNull(Long id);

	Optional<Record> findOneByEndTimeIsNull();

	List<Record> findAllByCreatedAtBetweenAndEndTimeIsNotNull(LocalDateTime start, LocalDateTime end);
}
