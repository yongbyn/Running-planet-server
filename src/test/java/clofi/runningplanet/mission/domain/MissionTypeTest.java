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

	@DisplayName("1000미터 이상 운동했을 시 true 반환")
	@ParameterizedTest
	@ValueSource(ints = {1000, 1001, 2000, 10000})
	void distanceIsCompleteReturnTrue(int value) {
		//given
		MissionType type = MissionType.DISTANCE;
		TodayRecords records = new TodayRecords(value, 500);

		//when
		boolean result = type.isComplete(records);

		//then
		assertThat(result).isTrue();
	}

	@DisplayName("1000미터 미만 운동했을 시 false 반환")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 500, 999})
	void distanceIsCompleteReturnFalse(int value) {
		//given
		MissionType type = MissionType.DISTANCE;
		TodayRecords records = new TodayRecords(value, 500);

		//when
		boolean result = type.isComplete(records);

		//then
		assertThat(result).isFalse();
	}

	@DisplayName("목표치까지 1이하의 미션 진행률 반환")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 500, 999, 1000})
	void distanceCalculateProgressLessThanEqual1(int value) {
		//given
		MissionType type = MissionType.DISTANCE;
		TodayRecords records = new TodayRecords(value, 500);

		//when
		double result = type.calculateProgress(records);

		//then
		assertThat(result).isEqualTo((double)value / type.getRequiredScore());
		assertThat(result).isLessThanOrEqualTo(1);
	}

	@DisplayName("목표치 초과 달성 시 진행률 1 초과 반환")
	@ParameterizedTest
	@ValueSource(ints = {1001, 1002, 1100, 3000, 9000})
	void distanceCalculateProgressOver1(int value) {
		//given
		MissionType type = MissionType.DISTANCE;
		TodayRecords records = new TodayRecords(value, 500);

		//when
		double result = type.calculateProgress(records);

		//then
		assertThat(result).isEqualTo((double)value / type.getRequiredScore());
		assertThat(result).isGreaterThan(1);
	}
}
