package clofi.runningplanet.crew.domain;

import static clofi.runningplanet.common.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;

class CrewMemberTest {

	private static final Member MEMBER = Member.builder()
		.id(1L)
		.nickname("닉네임")
		.age(20)
		.gender(Gender.MALE)
		.profileImg("https://image-url.com")
		.avgDistance(10)
		.totalDistance(100)
		.build();

	@DisplayName("리더 검증 로직 성공")
	@Test
	void successCheckLeaderPrivilege() {
		//given
		Crew crew = createCrew();
		CrewMember crewMember = new CrewMember(1L, crew, MEMBER, Role.LEADER);

		//when
		//then
		assertDoesNotThrow(crewMember::checkLeaderPrivilege);
	}

	@DisplayName("리더가 아닌 경우 예외 발생")
	@Test
	void failCheckLeaderPrivilege() {
		//given
		Crew crew = createCrew();
		CrewMember crewMember = new CrewMember(1L, crew, MEMBER, Role.MEMBER);

		//when
		//then
		assertThatThrownBy(crewMember::checkLeaderPrivilege)
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("크루에 속한 인원일 경우 성공")
	@Test
	void successValidateMembership() {
		//given
		Crew crew = createCrew();
		CrewMember crewMember = new CrewMember(1L, crew, MEMBER, Role.MEMBER);

		//when
		//then
		assertDoesNotThrow(() -> crewMember.validateMembership(1L));
	}

	@DisplayName("크루에 속해 있지 않은 경우 예외 발생")
	@ParameterizedTest
	@ValueSource(longs = {2L, 4L, 100L, 1832L})
	void failValidateMembership(Long crewId) {
		//given
		Crew crew = createCrew();
		CrewMember crewMember = new CrewMember(1L, crew, MEMBER, Role.MEMBER);

		//when
		//then
		assertThatThrownBy(() -> crewMember.validateMembership(crewId))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
