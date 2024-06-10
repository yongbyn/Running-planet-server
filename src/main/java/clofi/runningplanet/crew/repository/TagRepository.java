package clofi.runningplanet.crew.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.crew.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	List<Tag> findAllByCrewId(Long id);

	void deleteAllByCrewId(Long crewId);
}
