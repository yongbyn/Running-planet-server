package clofi.runningplanet.mission.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clofi.runningplanet.mission.domain.vo.TodayRecords;

class MissionTypeTest {

	@DisplayName("3600초 이상 운동했을 시 true 반환")
	@ParameterizedTest
	@ValueSource(ints = {3600, 3601, 4000, 10000})
	void durationIsCompleteReturnTrue(int value) {
		//given
		MissionType type = MissionType.DURATION;
		TodayRecords records = new TodayRecords(500, value);

		//when
		boolean result = type.isComplete(records);

		//then
		assertThat(result).isTrue();
	}

	@DisplayName("3600초 미만으로 운동했을 시 false 반환")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2000, 3599})
	void durationIsCompleteReturnFalse(int value) {
		//given
		MissionType type = MissionType.DURATION;
		TodayRecords records = new TodayRecords(500, value);

		//when
		boolean result = type.isComplete(records);

		//then
		assertThat(result).isFalse();
	}
}
