package clofi.runningplanet.planet.dto.response;

import clofi.runningplanet.planet.domain.Planet;

public record PlanetResponse(
	Long planetId,
	String planetName,
	String planetImage,
	Integer demandDistance,
	Integer distance
) {
	public PlanetResponse(Planet planet, String planetName, String planetImage, Integer demandDistance,
		Integer distance) {
		this(planet.getPlanetImageId(),
			planetName, planetImage, demandDistance, distance);
	}

}
