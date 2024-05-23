package clofi.runningplanet.running.dto;

import clofi.runningplanet.running.domain.Coordinate;

public record CoordinateResponse(
	double latitude,
	double longitude
) {
	public CoordinateResponse(Coordinate coordinate) {
		this(coordinate.getLatitude(), coordinate.getLongitude());
	}
}
