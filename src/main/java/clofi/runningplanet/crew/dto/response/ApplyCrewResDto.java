package clofi.runningplanet.crew.dto.response;

public record ApplyCrewResDto(
	Long crewId,
	Long memberId,
	boolean isRequest
) {
}
