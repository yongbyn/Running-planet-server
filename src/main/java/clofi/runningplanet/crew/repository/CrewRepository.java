package clofi.runningplanet.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import clofi.runningplanet.crew.domain.Crew;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long>, CrewRepositoryCustom {
}
