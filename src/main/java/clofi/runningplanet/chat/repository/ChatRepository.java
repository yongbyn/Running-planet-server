package clofi.runningplanet.chat.repository;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.chat.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	Page<Chat> findByCrewId(Long crewId, Pageable pageable);

	Page<Chat> findByCrewIdAndCreatedAtLessThan(Long crewId, LocalDateTime createdAt, Pageable pageable);
}
