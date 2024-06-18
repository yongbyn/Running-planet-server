package clofi.runningplanet.rank.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;

@Data
public class PersonalRankResponse {
	private Long id;
	private String nickname;
	private Integer planetCnt;
	private Integer distance;

	@QueryProjection
	public PersonalRankResponse(Long id, String nickname, Integer planetCnt, Integer distance) {
		this.id = id;
		this.nickname = nickname;
		this.planetCnt = planetCnt;
		this.distance = distance;
	}
}
