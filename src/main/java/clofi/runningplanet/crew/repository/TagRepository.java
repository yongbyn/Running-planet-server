package clofi.runningplanet.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.crew.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
