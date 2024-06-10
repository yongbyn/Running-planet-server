package clofi.runningplanet.crew.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import clofi.runningplanet.crew.domain.CrewImage;

@Repository
public interface CrewImageRepository extends JpaRepository<CrewImage, Long> {
	Optional<CrewImage> findByCrewId(Long crewId);
}
