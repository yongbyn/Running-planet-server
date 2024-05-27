package clofi.runningplanet.crew.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrewApplicationTest {

	@DisplayName("크루 가입 대기 상태에서 다시 신청할 경우 예외 발생")
	@Test
	void failApplyByPending() {
		//given
		CrewApplication crewApplication = new CrewApplication(1L, "크루 신청서", Approval.PENDING, null, null);

		//when
		//then
		assertThatThrownBy(crewApplication::checkDuplicateApply)
			.isInstanceOf(IllegalArgumentException.class);

	}
}
