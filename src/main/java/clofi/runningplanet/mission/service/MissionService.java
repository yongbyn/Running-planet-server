package clofi.runningplanet.mission.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.common.exception.ForbiddenException;
import clofi.runningplanet.common.exception.InternalServerException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.domain.vo.TodayRecords;
import clofi.runningplanet.mission.dto.response.CrewMissionListDto;
import clofi.runningplanet.mission.dto.response.GetCrewMissionResDto;
import clofi.runningplanet.mission.repository.CrewMissionRepository;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.repository.RecordRepository;
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

		List<CrewMission> crewMissionList = getTodayCrewMissionList(crewId, memberId);

		TodayRecords todayRecords = getTodayRecords(memberId);
		List<GetCrewMissionResDto> resDtoList = convertToResDto(crewMissionList, todayRecords);

		return new CrewMissionListDto(resDtoList);
	}

	@Transactional
	public void successMission(Long crewId, Long missionId, Long memberId) {
		checkMemberExist(memberId);
		validateCrewMemberShip(crewId, memberId);

		CrewMission findMission = getFindMission(missionId);
		findMission.validateComplete();

		TodayRecords todayRecords = getTodayRecords(memberId);
		validateRecords(findMission, todayRecords);

		findMission.completeMission();

		Crew findCrew = getFindCrew(crewId);
		findCrew.gainExp(10);
	}

	@Transactional
	public void createDailyMission() {
		List<CrewMember> crewMemberList = crewMemberRepository.findAll();

		LocalDate now = LocalDate.now();
		LocalDateTime startOfDay = now.atStartOfDay();
		LocalDateTime endOfDay = now.atTime(LocalTime.MAX);

		for (CrewMember crewMember : crewMemberList) {
			if (crewMissionRepository.findAllByCrewIdAndMemberIdAndToday(crewMember.getCrew().getId(),
				crewMember.getMember().getId(), startOfDay, endOfDay).isEmpty()) {
				List<CrewMission> dailyMissionList = List.of(
					new CrewMission(crewMember.getMember(), crewMember.getCrew(), MissionType.DISTANCE),
					new CrewMission(crewMember.getMember(), crewMember.getCrew(),
						MissionType.DURATION));
				crewMissionRepository.saveAll(dailyMissionList);
			}
		}
	}

	private List<GetCrewMissionResDto> convertToResDto(List<CrewMission> crewMissionList,
		TodayRecords todayRecords) {
		return crewMissionList.stream()
			.map(crewMission -> convertToDto(crewMission, todayRecords))
			.toList();
	}

	private void validateMissionListSize(List<CrewMission> crewMissionList) {
		if (crewMissionList.size() != MissionType.values().length) {
			throw new InternalServerException();
		}
	}

	private List<CrewMission> getTodayCrewMissionList(Long crewId, Long memberId) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

		return crewMissionRepository.findAllByCrewIdAndMemberIdAndToday(crewId, memberId,
			startOfDay, endOfDay);
	}

	private void validateRecords(CrewMission mission, TodayRecords todayRecords) {
		boolean isMissionCompleted = mission.isMissionComplete(todayRecords);

		if (!isMissionCompleted) {
			throw new ConflictException("미션 완료 조건을 달성하지 못했습니다.");
		}
	}

	private CrewMission getFindMission(Long missionId) {
		return crewMissionRepository.findById(missionId).orElseThrow(
			() -> new NotFoundException("해당 미션이 존재하지 않습니다.")
		);
	}

	private Crew getFindCrew(Long crewId) {
		return crewRepository.findById(crewId).orElseThrow(
			() -> new NotFoundException("해당 크루를 찾을 수 없습니다.")
		);
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
		double result = calculateProgressInPercent(crewMission, todayRecords);

		return new GetCrewMissionResDto(crewMission.getId(), crewMission.getType(), result, crewMission.isCompleted());
	}

	private double calculateProgressInPercent(CrewMission crewMission, TodayRecords todayRecords) {
		return crewMission.calculateProgress(todayRecords);
	}

	private TodayRecords getTodayRecords(Long memberId) {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
		List<Record> todayRecordList = recordRepository.findAllByMemberIdAndCreatedAtBetween(memberId, start, end);

		return new TodayRecords(todayRecordList);
	}
}
