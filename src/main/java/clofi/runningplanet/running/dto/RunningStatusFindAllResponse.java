package clofi.runningplanet.running.dto;

import java.util.List;

import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.running.domain.Record;

public record RunningStatusFindAllResponse(
	Long memberId,
	String nickname,
	String profileImg,
	int runTime,
	double runDistance,
	boolean isEnd,
	boolean canCheer
) {
	public RunningStatusFindAllResponse(Member member) {
		this(member.getId(), member.getNickname(), member.getProfileImg(), 0, 0, true, false);
	}

	public RunningStatusFindAllResponse(List<Record> recordList, boolean canCheer) {
		this(recordList.getFirst().getMember().getId(),
			recordList.getFirst().getMember().getNickname(),
			recordList.getFirst().getMember().getProfileImg(),
			recordList.stream().mapToInt(Record::getRunTime).sum(),
			recordList.stream().mapToDouble(Record::getRunDistance).sum(),
			recordList.stream().allMatch(r -> r.getEndTime() != null),
			canCheer);
	}
}
