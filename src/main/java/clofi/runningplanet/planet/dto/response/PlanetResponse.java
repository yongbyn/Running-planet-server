package clofi.runningplanet.planet.dto.response;

import clofi.runningplanet.planet.domain.Planet;

public record PlanetResponse(
	Long planetId,
	String planetName,
	String planetImage,
	Double demandDistance,
	Double distance
) {
	public PlanetResponse(Planet planet, String planetName, String planetImage, Double demandDistance,
		Double distance) {
		this(planet.getPlanetImageId(),
			planetName, planetImage, demandDistance, distance);
	}

}
