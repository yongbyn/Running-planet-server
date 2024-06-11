package clofi.runningplanet.planet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.runningplanet.planet.domain.Planet;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
}
