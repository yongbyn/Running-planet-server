package clofi.runningplanet.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.crew.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
