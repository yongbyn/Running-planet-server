package clofi.runningplanet.member.dto.response;

import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.member.domain.Gender;
import clofi.runningplanet.member.domain.Member;

public record SelfProfileResponse(
	String nickname,

	Gender gender,

	Integer age,

	Integer weight,

	String profileImg,

	AvgPace avgPace,

	double avgDistance,

	double totalDistance,

	String myCrew,

	Long myCrewId,

	Long memberId
) {
	public SelfProfileResponse(Member member, CrewMember crewMember) {
		this(member.getNickname(), member.getGender(), member.getAge(), member.getWeight(), member.getProfileImg(),
			calculateAvgPace(member.getAvgPace())
			, member.getAvgDistance(), member.getTotalDistance(),
			crewMember != null ? crewMember.getCrew().getCrewName() : null,
			(crewMember != null && crewMember.getCrew().getId() != null) ? crewMember.getCrew().getId() : null,
			member.getId());
	}

	public record AvgPace(
		int min,

		int sec
	) {
	}

	private static AvgPace calculateAvgPace(int totalSec) {
		int min = totalSec / 60;
		int sec = totalSec % 60;
		return new AvgPace(min, sec);
	}
}
