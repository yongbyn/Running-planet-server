package clofi.runningplanet.mission.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.common.exception.ForbiddenException;
import clofi.runningplanet.common.exception.InternalServerException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.dto.response.GetCrewMissionResDto;
import clofi.runningplanet.mission.repository.CrewMissionRepository;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.repository.RecordRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MissionService {

	private final CrewMissionRepository crewMissionRepository;
	private final CrewRepository crewRepository;
	private final MemberRepository memberRepository;
	private final CrewMemberRepository crewMemberRepository;
	private final RecordRepository recordRepository;

	@Transactional(readOnly = true)
	public CrewMissionListDto getCrewMission(Long crewId, Long memberId) {
		checkCrewExist(crewId);
		checkMemberExist(memberId);
		validateCrewMemberShip(crewId, memberId);

		List<CrewMission> crewMissionList = crewMissionRepository.findAllByCrewIdAndMemberId(crewId, memberId);
		if (crewMissionList.size() != 2) {
			throw new InternalServerException();
		}

		TodayRecords todayRecords = getTodayRecords(memberId);

		List<GetCrewMissionResDto> resDtoList = crewMissionList.stream()
			.map(crewMission -> convertToDto(crewMission, todayRecords))
			.toList();
		return new CrewMissionListDto(resDtoList);
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

	private GetCrewMissionResDto convertToDto(CrewMission crewMission, TodayRecords todayRecords) {
		double result = crewMission.getType() == MissionType.DISTANCE ?
			(double)(todayRecords.getTotalDistance() / 1000) :
			(double)(todayRecords.getTotalDuration() / 3600);

		return new GetCrewMissionResDto(crewMission.getId(), crewMission.getType(), result, crewMission.isCompleted());
	}

	private TodayRecords getTodayRecords(Long memberId) {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
		List<Record> todayRecordList = recordRepository.findAllByMemberIdAndCreatedAtBetween(memberId, start, end);

		int todayTotalDistance = todayRecordList.stream().mapToInt(Record::getRunDistance).sum();
		int todayTotalDuration = todayRecordList.stream().mapToInt(Record::getRunTime).sum();

		return new TodayRecords(todayTotalDistance, todayTotalDuration);
	}

	@Getter
	private static class TodayRecords {
		private final int totalDistance;
		private final int totalDuration;

		private TodayRecords(int totalDistance, int totalDuration) {
			this.totalDistance = totalDistance;
			this.totalDuration = totalDuration;
		}

	}
}
