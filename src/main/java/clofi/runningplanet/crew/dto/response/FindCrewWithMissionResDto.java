package clofi.runningplanet.crew.dto.response;

import java.util.List;

import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.RuleDto;

public record FindCrewWithMissionResDto(
	Long crewId,
	int crewLevel,
	String crewName,
	String introduction,
	int memberCnt,
	int limitMemberCnt,
	List<String> tags,
	Category category,
	RuleDto rule,
	int crewTotalDistance,
	List<Double> missionProgress,
	boolean isCrewLeader,
	String imgFile
) {
	public FindCrewWithMissionResDto(Crew crew, List<String> tags, String imgPath, List<Double> missionProgress,
		int memberCnt, boolean isCrewLeader) {
		this(crew.getId(), crew.getCrewLevel(), crew.getCrewName(), crew.getIntroduction(), memberCnt,
			crew.getLimitMemberCnt(), tags,
			crew.getCategory(), new RuleDto(crew.getRuleRunCnt(), crew.getRuleDistance()), crew.getTotalDistance(),
			missionProgress, isCrewLeader, imgPath);
	}
}
