package clofi.runningplanet.running.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.running.domain.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {
	Optional<Record> findByIdAndEndTimeIsNotNull(Long id);
}
