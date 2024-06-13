package clofi.runningplanet.running.dto;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.running.domain.Record;

public record RunningStatusResponse(
	Long memberId,
	String nickname,
	int runTime,
	double runDistance,
	boolean isEnd
) {
	public RunningStatusResponse(Member member, Record savedRecord) {
		this(member.getId(), member.getNickname(), savedRecord.getRunTime(),
			savedRecord.getRunDistance(), savedRecord.isEnd());
	}

	public RunningStatusResponse(Member member) {
		this(member.getId(), member.getNickname(), 0, 0, true);
	}
}
