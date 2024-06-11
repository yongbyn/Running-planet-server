package clofi.runningplanet.mission.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clofi.runningplanet.mission.domain.vo.TodayRecords;

class MissionTypeTest {

	@DisplayName("3600초 이상 운동했을 시 미션 성공 판단 로직 성공")
	@ParameterizedTest
	@ValueSource(ints = {3600, 3601, 4000, 10000})
	void successIsComplete(int value) {
		//given
		MissionType type = MissionType.DURATION;
		TodayRecords records = new TodayRecords(500, value);

		//when
		boolean result = type.isComplete(records);

		//then
		assertThat(result).isTrue();
	}
}
