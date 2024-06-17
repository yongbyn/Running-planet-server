package clofi.runningplanet.rank.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;

@Data
public class CrewRankResponse {
	private Long id;
	private String crewName;
	private Integer level;
	private Integer distance;

	@QueryProjection
	public CrewRankResponse(Long id, String crewName, Integer level, Integer distance) {
		this.id = id;
		this.crewName = crewName;
		this.level = level;
		this.distance = distance;
	}
}
