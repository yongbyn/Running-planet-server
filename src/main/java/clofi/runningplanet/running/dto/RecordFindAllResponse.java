package clofi.runningplanet.running.dto;

import clofi.runningplanet.running.domain.Record;

public record RecordFindAllResponse(
	Long id,
	int runDistance,
	int day
) {
	public RecordFindAllResponse(Record record) {
		this(record.getId(), record.getRunDistance(), record.getCreatedAt().getDayOfMonth());
	}
}
