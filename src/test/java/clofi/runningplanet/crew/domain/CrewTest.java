package clofi.runningplanet.crew.domain;

import static clofi.runningplanet.crew.domain.ApprovalType.*;
import static clofi.runningplanet.crew.domain.Category.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CrewTest {

	@DisplayName("크루 제한 인원수 검증 통과")
	@ParameterizedTest()
	@ValueSource(ints = {1, 5, 9})
	void successCheckReachedMemberLimit(int currentMemberCnt) {
		//given
		Crew crew = new Crew(1L, 2L, "구름 크루", 10,
			RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100,
			0, 0, 0, 1);

		//when
		boolean result = crew.checkReachedMemberLimit(currentMemberCnt);

		//then
		assertThat(result).isFalse();
	}

	@DisplayName("크루 제한 인원수 검증 통과")
	@ParameterizedTest()
	@ValueSource(ints = {10, 11, 15, 100})
	void failCheckReachedMemberLimit(int currentMemberCnt) {
		//given
		Crew crew = new Crew(1L, 2L, "구름 크루", 10,
			RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100,
			0, 0, 0, 1);

		//when
		boolean result = crew.checkReachedMemberLimit(currentMemberCnt);

		//then
		assertThat(result).isTrue();
	}

	@DisplayName("요구 경험치 보다 적은 양의 경험치를 얻을 경우 레벨 유지 및 경험치 증가")
	@ParameterizedTest()
	@ValueSource(ints = {0, 10, 100, 199})
	void gainExpUnderRequiredExp(int value) {
		//given
		Crew crew = new Crew(1L, 2L, "구름 크루", 10,
			RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100,
			0, 0, 0, 1);

		//when
		crew.gainExp(value);

		//then
		assertThat(crew.getCrewLevel()).isEqualTo(1);
		assertThat(crew.getCrewExp()).isEqualTo(value);
		assertThat(crew.getLimitMemberCnt()).isEqualTo(10);
	}

	@DisplayName("요구 경험치 보다 적은 양의 경험치를 얻을 경우 레벨 유지 및 경험치 증가")
	@Test
	void gainExpEqualRequiredExp() {
		//given
		Crew crew = new Crew(1L, 2L, "구름 크루", 10,
			RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100,
			0, 0, 0, 1);
		int beforeCrewLevel = crew.getCrewLevel();
		int beforeLimitMemberCnt = crew.getLimitMemberCnt();

		//when
		crew.gainExp(crew.getRequiredExp());

		//then
		assertThat(crew.getCrewLevel()).isEqualTo(beforeCrewLevel + 1);
		assertThat(crew.getCrewExp()).isEqualTo(0);
		assertThat(crew.getLimitMemberCnt()).isEqualTo(beforeLimitMemberCnt + 5);
	}

	@DisplayName("요구 경험치 보다 많은 양의 경험치를 얻을 경우 레벨 변경")
	@ParameterizedTest()
	@ValueSource(ints = {201, 300, 10000})
	void gainExpOverRequiredExp(int value) {
		//given
		Crew crew = new Crew(1L, 2L, "구름 크루", 10,
			RUNNING, MANUAL, "구름 크루는 성실한 크루", 5, 100,
			0, 0, 0, 1);

		//when
		crew.gainExp(value);

		//then
		assertThat(crew.getCrewLevel()).isGreaterThan(1);
		assertThat(crew.getCrewExp()).isLessThan(value);
		assertThat(crew.getLimitMemberCnt()).isGreaterThan(10);
	}
}
