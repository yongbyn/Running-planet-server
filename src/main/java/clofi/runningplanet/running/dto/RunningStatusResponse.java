package clofi.runningplanet.running.dto;

import java.util.List;

import clofi.runningplanet.running.domain.Record;

public record RunningStatusResponse(
	Long memberId,
	String nickname,
	int runTime,
	double runDistance,
	boolean isEnd
) {
	public RunningStatusResponse(List<Record> recordList) {
		this(recordList.getFirst().getMember().getId(),
			recordList.getFirst().getMember().getNickname(),
			recordList.stream().mapToInt(Record::getRunTime).sum(),
			recordList.stream().mapToDouble(Record::getRunDistance).sum(),
			recordList.stream().allMatch(r -> r.getEndTime() != null));
	}
}
