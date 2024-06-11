package clofi.runningplanet.planet.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePlanetNameRequest(
	@NotNull String planetName
) {
}
