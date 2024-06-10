package clofi.runningplanet.mission.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.common.exception.ForbiddenException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MissionService {

	private final CrewRepository crewRepository;
	private final MemberRepository memberRepository;
	private final CrewMemberRepository crewMemberRepository;

	@Transactional(readOnly = true)
	public CrewMissionListDto getCrewMission(Long crewId, Long memberId) {
		checkCrewExist(crewId);
		checkMemberExist(memberId);
		validateCrewMemberShip(crewId, memberId);
	}

	private void checkMemberExist(Long memberId) {
		if (!memberRepository.existsById(memberId)) {
			throw new NotFoundException("인증된 사용자가 아닙니다.");
		}
	}

	private void checkCrewExist(Long crewId) {
		if (!crewRepository.existsById(crewId)) {
			throw new NotFoundException("크루가 존재하지 않습니다.");
		}
	}

	private void validateCrewMemberShip(Long crewId, Long memberId) {
		if (!crewMemberRepository.existsByCrewIdAndMemberId(crewId, memberId)) {
			throw new ForbiddenException("소속된 크루가 아닙니다.");
		}
	}
}
