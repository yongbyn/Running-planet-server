package clofi.runningplanet.running.service;

import static clofi.runningplanet.common.utils.TimeUtils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.running.domain.Cheer;
import clofi.runningplanet.running.domain.Coordinate;
import clofi.runningplanet.running.domain.Record;
import clofi.runningplanet.running.dto.CheerResponse;
import clofi.runningplanet.running.dto.RecordFindAllResponse;
import clofi.runningplanet.running.dto.RecordFindCurrentResponse;
import clofi.runningplanet.running.dto.RecordFindResponse;
import clofi.runningplanet.running.dto.RecordSaveRequest;
import clofi.runningplanet.running.dto.RunningStatusFindAllResponse;
import clofi.runningplanet.running.dto.RunningStatusResponse;
import clofi.runningplanet.running.repository.CheerRepository;
import clofi.runningplanet.running.repository.CoordinateRepository;
import clofi.runningplanet.running.repository.RecordRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecordService {
	private final RecordRepository recordRepository;
	private final CoordinateRepository coordinateRepository;
	private final MemberRepository memberRepository;
	private final CrewMemberRepository crewMemberRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final CheerRepository cheerRepository;

	@Transactional
	public Record save(RecordSaveRequest request, Long memberId) {
		Member member = getMember(memberId);
		Record record = getCurrentRecordOrElseNew(member);

		record.update(request.runTime(), request.runDistance(), request.calories(), request.avgPace().min(),
			request.avgPace().sec(), request.isEnd());

		Record savedRecord = recordRepository.save(record);

		Coordinate coordinate = request.toCoordinate(savedRecord);
		coordinateRepository.save(coordinate);

		if (savedRecord.isEnd()) {
			updateRunningStatistics(member);
			member.increaseExp(savedRecord.getRunDistance());
		}

		LocalDate now = LocalDate.now();
		LocalDateTime start = getStartOfDay(now);
		LocalDateTime end = getEndOfDay(now);
		List<Record> records = recordRepository.findAllByMemberIdAndCreatedAtBetween(
			member.getId(), start, end);

		RunningStatusResponse runningStatusResponse = new RunningStatusResponse(records);
		sendRunningStatus(member, runningStatusResponse);

		return savedRecord;
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}

	private Record getCurrentRecordOrElseNew(Member member) {
		return recordRepository.findOneByMemberAndEndTimeIsNull(member)
			.orElse(Record.builder().member(member).build());
	}

	private void updateRunningStatistics(Member member) {
		List<Record> records = recordRepository.findAllByMember(member);
		int totalRunTime = records.stream().mapToInt(Record::getRunTime).sum();
		double totalRunDistance = records.stream().mapToDouble(Record::getRunDistance).sum();
		member.updateRunningStatistics(totalRunTime, totalRunDistance, records.size());
	}

	private void sendRunningStatus(Member member, RunningStatusResponse runningStatusResponse) {
		crewMemberRepository.findByMemberId(member.getId())
			.ifPresent(crewMember -> messagingTemplate.convertAndSend(
				String.format("/sub/crew/%s/running", crewMember.getCrew().getId()),
				runningStatusResponse));
	}

	public List<RecordFindAllResponse> findAll(Integer year, Integer month, Long memberId) {
		Member member = getMember(memberId);

		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDateTime start = getStartOfDay(yearMonth.atDay(1));
		LocalDateTime end = getEndOfDay(yearMonth.atEndOfMonth());
		List<Record> records = recordRepository.findAllByMemberAndCreatedAtBetweenAndEndTimeIsNotNull(member, start,
			end);

		return records.stream()
			.map(RecordFindAllResponse::new)
			.toList();
	}

	public RecordFindResponse find(Long recordId, Long memberId) {
		Member member = getMember(memberId);
		Record record = getCurrentRecord(recordId, member);
		List<Coordinate> coordinates = coordinateRepository.findAllByRecord(record);

		return new RecordFindResponse(record, coordinates);
	}

	private Record getCurrentRecord(Long recordId, Member member) {
		return recordRepository.findByIdAndMemberAndEndTimeIsNotNull(recordId, member)
			.orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));
	}

	public RecordFindCurrentResponse findCurrentRecord(Long memberId) {
		Member member = getMember(memberId);
		Optional<Record> optionalRecord = recordRepository.findOneByMemberAndEndTimeIsNull(member);
		if (optionalRecord.isEmpty()) {
			return null;
		}
		Record record = optionalRecord.get();
		Coordinate coordinate = getLastCoordinate(record);

		return new RecordFindCurrentResponse(record, coordinate);
	}

	private Coordinate getLastCoordinate(Record record) {
		return coordinateRepository.findFirstByRecordOrderByCreatedAtDesc(record)
			.orElseThrow(() -> new IllegalArgumentException("좌표 정보를 찾을 수 없습니다."));
	}

	@Transactional
	public List<RunningStatusFindAllResponse> findAllRunningStatus(Long memberId, Long crewId) {
		if (!crewMemberRepository.existsByCrewIdAndMemberId(crewId, memberId)) {
			throw new IllegalArgumentException("크루에 소속된 회원이 아닙니다.");
		}

		List<Member> members = crewMemberRepository.findMembersByCrewId(crewId);

		LocalDate now = LocalDate.now();
		LocalDateTime start = getStartOfDay(now);
		LocalDateTime end = getEndOfDay(now);
		List<Record> records = recordRepository.findAllByMemberInAndCreatedAtBetween(members, start, end);

		List<RunningStatusFindAllResponse> runningStatusResponses = convertToRunningStatusResponses(memberId, members, records,
			start, end);
		sortByIsEndAndRunTime(runningStatusResponses);

		return runningStatusResponses;
	}

	private List<RunningStatusFindAllResponse> convertToRunningStatusResponses(Long fromMemberId, List<Member> members,
		List<Record> records,
		LocalDateTime start, LocalDateTime end) {
		Map<Long, List<Record>> groupedByMemberId = records.stream()
			.collect(Collectors.groupingBy(record -> record.getMember().getId()));

		List<RunningStatusFindAllResponse> runningStatusFindAllResponses = new ArrayList<>();
		for (Member member : members) {
			List<Record> memberRecords = groupedByMemberId.get(member.getId());
			if (memberRecords == null) {
				runningStatusFindAllResponses.add(new RunningStatusFindAllResponse(member));
			} else {
				boolean canCheer = cheerRepository
					.findCheerByFromMemberIdAndToMemberIdAndCreatedAtIsBetween(fromMemberId, member.getId(), start, end)
					.isEmpty();
				runningStatusFindAllResponses.add(new RunningStatusFindAllResponse(memberRecords, canCheer));
			}
		}

		return runningStatusFindAllResponses;
	}

	private void sortByIsEndAndRunTime(List<RunningStatusFindAllResponse> runningStatusResponses) {
		runningStatusResponses.sort((r1, r2) -> {
			if (r1.isEnd() != r2.isEnd()) {
				return Boolean.compare(r1.isEnd(), r2.isEnd());
			}
			return Integer.compare(r2.runTime(), r1.runTime());
		});
	}

	@Transactional
	public void sendCheering(Long crewId, Long fromMemberId, Set<Long> toMemberIds) {
		Member fromMember = getMember(fromMemberId);
		if (!crewMemberRepository.existsByCrewIdAndMemberId(crewId, fromMemberId)) {
			throw new IllegalArgumentException("크루에 소속된 회원이 아닙니다.");
		}

		List<Member> toMembers = crewMemberRepository.findMembersByCrewAndMemberIds(crewId, toMemberIds);
		if (toMembers.isEmpty()) {
			return;
		}

		LocalDate now = LocalDate.now();
		LocalDateTime start = getStartOfDay(now);
		LocalDateTime end = getEndOfDay(now);

		List<Record> records = recordRepository
			.findAllByEndTimeIsNullAndCreatedAtBetweenAndMemberIn(start, end, toMembers);

		records.stream()
			.filter(record -> cheerRepository.findAllByFromMemberAndToMemberAndCreatedAtIsBetween(fromMember,
				record.getMember(), start, end).isEmpty())
			.forEach(record -> saveAndSend(fromMember, record.getMember(), crewId));
	}

	private void saveAndSend(Member fromMember, Member toMember, Long crewId) {
		cheerRepository.save(new Cheer(fromMember, toMember));
		messagingTemplate.convertAndSendToUser(String.valueOf(toMember), String.format("/sub/crew/%s/cheer", crewId),
			new CheerResponse(fromMember.getId(), fromMember.getNickname()));
	}
}
