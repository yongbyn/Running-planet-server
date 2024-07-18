package clofi.runningplanet.crew.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.common.exception.UnauthorizedException;
import clofi.runningplanet.common.service.S3StorageManagerUseCase;
import clofi.runningplanet.crew.domain.Approval;
import clofi.runningplanet.crew.domain.ApprovalType;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.crew.domain.CrewImage;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.domain.Tag;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.SearchParamDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.request.ProceedApplyReqDto;
import clofi.runningplanet.crew.dto.request.UpdateCrewReqDto;
import clofi.runningplanet.crew.dto.response.ApplyCrewResDto;
import clofi.runningplanet.crew.dto.response.ApprovalMemberResDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewMemberResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewWithMissionResDto;
import clofi.runningplanet.crew.dto.response.GetApplyCrewResDto;
import clofi.runningplanet.crew.repository.CrewApplicationRepository;
import clofi.runningplanet.crew.repository.CrewImageRepository;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.repository.TagRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import clofi.runningplanet.mission.domain.CrewMission;
import clofi.runningplanet.mission.domain.MissionType;
import clofi.runningplanet.mission.repository.CrewMissionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CrewService {

	private final CrewRepository crewRepository;
	private final TagRepository tagRepository;
	private final MemberRepository memberRepository;
	private final CrewApplicationRepository crewApplicationRepository;
	private final CrewMemberRepository crewMemberRepository;
	private final S3StorageManagerUseCase storageManagerUseCase;
	private final CrewImageRepository crewImageRepository;
	private final CrewMissionRepository crewMissionRepository;

	@Transactional
	public Long createCrew(CreateCrewReqDto reqDto, MultipartFile imageFile, Long memberId) {
		Member findMember = getMemberByMemberId(memberId);
		checkSubscribedCrew(memberId);

		Crew savedCrew = createAndSaveCrew(reqDto, memberId);
		saveTags(reqDto.tags(), savedCrew);
		saveCrewImage(imageFile, savedCrew);
		createAndSaveCrewMember(savedCrew, findMember);
		saveInitialCrewMission(savedCrew, findMember);
		return savedCrew.getId();
	}

	@Transactional(readOnly = true)
	public List<FindAllCrewResDto> findAllCrew(SearchParamDto searchParamDto) {
		List<Crew> crewSearchList = crewRepository.search(searchParamDto);

		return crewSearchList.stream()
			.map(this::convertToFindAllCrewResDto)
			.toList();
	}

	@Transactional(readOnly = true)
	public FindCrewResDto findCrew(Long crewId) {
		Crew findCrew = getCrewByCrewId(crewId);
		int memberCnt = getCrewMemberCnt(crewId);

		List<String> tags = findTagsToStrings(findCrew.getId());
		CrewLeaderDto crewLeader = convertCrewLeaderDto(findCrew.getLeaderId());
		CrewImage crewImage = findImage(crewId);

		return FindCrewResDto.of(findCrew, memberCnt, crewLeader, tags, crewImage.getFilepath());
	}

	@Transactional
	public ApplyCrewResDto applyCrew(ApplyCrewReqDto reqDto, Long crewId, Long memberId) {

		Member findMember = getMemberByMemberId(memberId);

		validateMemberNotInCrew(findMember.getId());
		validateDuplicateApply(crewId, findMember.getId());

		Crew findCrew = getCrewByCrewId(crewId);

		if (findCrew.getApprovalType() == ApprovalType.AUTO) {
			handleAutoApproval(findCrew, findMember);
		} else {
			saveCrewApplication(reqDto, findCrew, findMember);
		}

		return new ApplyCrewResDto(crewId, findMember.getId(), true);
	}

	@Transactional(readOnly = true)
	public ApprovalMemberResDto getApplyCrewList(Long crewId, Long memberId) {
		validateLeaderPrivilege(crewId, memberId);
		checkCrewExistById(crewId);

		List<CrewApplication> crewApplicationList = crewApplicationRepository.findAllByCrewIdAndApproval(crewId,
			Approval.PENDING);
		List<GetApplyCrewResDto> getApplyCrewResDtos = makeGetApplyDtoList(crewApplicationList);
		return new ApprovalMemberResDto(getApplyCrewResDtos);
	}

	@Transactional
	public void proceedApplyCrew(ProceedApplyReqDto reqDto, Long crewId, Long memberId) {
		Crew findCrew = getCrewByCrewId(crewId);
		validateLeaderPrivilege(crewId, memberId);

		CrewApplication crewApplication = getCrewApplicationByCrewIdAndMemberId(crewId, reqDto.memberId());
		validateMemberNotInCrew(reqDto.memberId());

		if (reqDto.isApproval()) {
			processApproval(reqDto, findCrew, crewApplication);
		} else {
			crewApplication.reject();
		}
	}

	@Transactional
	public void removeCrewMember(Long crewId, Long memberId, Long leaderId) {
		checkCrewExistById(crewId);
		validateLeaderPrivilege(crewId, leaderId);
		checkMemberExist(memberId);

		CrewMember crewMember = findCrewMember(crewId, memberId);
		deleteCrewMember(crewMember);
	}

	@Transactional
	public void leaveCrew(Long crewId, Long memberId) {
		checkCrewExistById(crewId);
		checkMemberExist(memberId);

		CrewMember crewMember = findCrewMember(crewId, memberId);
		validateLeaderLeaveCrew(crewId, crewMember);
	}

	@Transactional
	public ApplyCrewResDto cancelCrewApplication(Long crewId, Long memberId) {
		checkCrewExistById(crewId);
		checkMemberExist(memberId);

		CrewApplication crewApplication = getCrewApplicationByCrewIdAndMemberId(crewId, memberId);
		processCancelApplication(crewApplication);
		return new ApplyCrewResDto(crewId, memberId, false);
	}

	@Transactional
	public void updateCrew(UpdateCrewReqDto reqDto, MultipartFile imgFile, Long crewId, Long memberId) {
		Crew findCrew = getCrewByCrewId(crewId);
		validateLeaderPrivilege(crewId, memberId);
		checkMemberExist(memberId);

		findCrew.update(reqDto.approvalType(), reqDto.introduction(), reqDto.rule());

		updateTags(reqDto, findCrew);

		if (imgFile != null && !imgFile.isEmpty()) {
			updateCrewImage(imgFile, crewId);
		}
	}

	@Transactional(readOnly = true)
	public FindCrewWithMissionResDto findCrewWithMission(Long crewId, Long memberId) {
		Crew findCrew = getCrewByCrewId(crewId);
		int memberCnt = getCrewMemberCnt(crewId);

		CrewMember crewMember = getCrewMember(crewId, memberId);
		boolean isCrewLeader = isCrewLeader(memberId, crewMember, findCrew);

		List<String> tags = findTagsToStrings(findCrew.getId());
		CrewImage crewImage = findImage(crewId);

		List<Double> crewMissionProgressUntilWeek = calculateCrewMissionProgressUntilWeek(crewId, memberCnt);

		return new FindCrewWithMissionResDto(findCrew, tags, crewImage.getFilepath(), crewMissionProgressUntilWeek,
			memberCnt, isCrewLeader);
	}

	@Transactional(readOnly = true)
	public List<FindCrewMemberResDto> findCrewMemberList(Long crewId, Long memberId) {
		checkCrewExistById(crewId);
		getCrewMember(crewId, memberId);

		List<CrewMember> crewMemberList = crewMemberRepository.findAllByCrewId(crewId);
		Map<Long, Long> missionCounts = getMissionCounts(crewId, crewMemberList);
		return convertToResDtos(crewMemberList, missionCounts);
	}

	private List<FindCrewMemberResDto> convertToResDtos(List<CrewMember> crewMemberList,
		Map<Long, Long> missionCounts) {
		return crewMemberList.stream()
			.map(crewMember -> new FindCrewMemberResDto(
				crewMember,
				Math.toIntExact(missionCounts.getOrDefault(crewMember.getMember().getId(), 0L))
			))
			.collect(Collectors.toList());
	}

	private Map<Long, Long> getMissionCounts(Long crewId, List<CrewMember> crewMemberList) {
		List<Long> memberIds = crewMemberList.stream()
			.map(crewMember -> crewMember.getMember().getId())
			.collect(Collectors.toList());

		List<CrewMission> missionList = crewMissionRepository.findByCrewIdAndMemberIds(crewId, memberIds);

		return missionList.stream()
			.filter(CrewMission::isCompleted)
			.collect(Collectors.groupingBy(
				mission -> mission.getMember().getId(),
				Collectors.counting()
			));
	}

	private void saveCrewImage(MultipartFile imageFile, Crew crew) {
		try {
			String originalFilename = imageFile.getOriginalFilename();
			String filepath = storageManagerUseCase.uploadImage(imageFile);
			CrewImage crewImage = new CrewImage(originalFilename, filepath, crew);
			crewImageRepository.save(crewImage);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	private void saveCrewApplication(ApplyCrewReqDto reqDto, Crew findCrew, Member findMember) {
		CrewApplication crewApplication = reqDto.toEntity(findCrew, findMember);
		crewApplicationRepository.save(crewApplication);
	}

	private void handleAutoApproval(Crew findCrew, Member findMember) {
		validateCrewMemberLimit(findCrew);

		CrewMember crewMember = CrewMember.createMember(findCrew, findMember);
		crewMemberRepository.save(crewMember);

		saveInitialCrewMission(findCrew, findMember);
	}

	private void saveInitialCrewMission(Crew findCrew, Member findMember) {
		List<CrewMission> firstCrewMissionList = Arrays.stream(MissionType.values())
			.map(value -> new CrewMission(findMember, findCrew, value))
			.collect(Collectors.toList());

		crewMissionRepository.saveAll(firstCrewMissionList);
	}

	private List<Double> calculateCrewMissionProgressUntilWeek(Long crewId, int memberCnt) {
		LocalDate startOfWeek = getStartOfWeek();
		LocalDate endOfWeek = startOfWeek.plusDays(6);

		List<CrewMission> crewMissionList = crewMissionRepository.findAllByCrewIdAndWeek(crewId,
			startOfWeek.atStartOfDay(),
			endOfWeek.atTime(23, 59, 59));

		Map<DayOfWeek, Double> result = calculateDayOfWeekSuccessRate(crewMissionList, memberCnt);

		return Arrays.stream(DayOfWeek.values())
			.map(result::get)
			.collect(Collectors.toList());
	}

	private Map<DayOfWeek, Double> calculateDayOfWeekSuccessRate(List<CrewMission> crewMissionList, int memberCnt) {
		return Arrays.stream(DayOfWeek.values())
			.collect(Collectors.toMap(
				day -> day,
				day -> {
					long completedCount = crewMissionList.stream()
						.filter(mission -> mission.getCreatedAt().getDayOfWeek() == day && mission.isCompleted())
						.count();
					double totalPossible = memberCnt * 2;
					return totalPossible == 0 ? 0.0 : (completedCount / totalPossible) * 100;
				}
			));
	}

	private LocalDate getStartOfWeek() {
		LocalDate now = LocalDate.now();
		return now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	private boolean isCrewLeader(Long memberId, CrewMember crewMember, Crew findCrew) {
		return crewMember.isLeader() && findCrew.getLeaderId().equals(memberId);
	}

	private CrewMember getCrewMember(Long crewId, Long memberId) {
		return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).orElseThrow(
			() -> new UnauthorizedException("크루에 소속된 크루원이 아닙니다.")
		);
	}

	private CrewImage findImage(Long crewId) {
		return crewImageRepository.findByCrewId(crewId).orElseThrow(
			() -> new NotFoundException("크루 이미지를 찾을 수 없습니다.")
		);
	}

	private void updateCrewImage(MultipartFile imgFile, Long crewId) {
		CrewImage findCrewImage = findImage(crewId);

		storageManagerUseCase.deleteImages(findCrewImage.getFilepath());
		String filePath = storageManagerUseCase.uploadImage(imgFile);
		findCrewImage.update(filePath, imgFile.getOriginalFilename());
	}

	private void updateTags(UpdateCrewReqDto reqDto, Crew crew) {
		tagRepository.deleteAllByCrewId(crew.getId());
		saveTags(reqDto.tags(), crew);
	}

	private void processCancelApplication(CrewApplication crewApplication) {
		crewApplication.cancel();
		crewApplicationRepository.deleteById(crewApplication.getId());
	}

	private void validateLeaderLeaveCrew(Long crewId, CrewMember crewMember) {
		if (!crewMember.isLeader()) {
			return;
		}

		int memberCnt = getCrewMemberCnt(crewId);
		if (memberCnt > 1) {
			throw new ConflictException("크루장은 크루원 수가 1인 일 경우에 탈퇴할 수 있습니다.");
		}
		deleteCrewMember(crewMember);
		deleteCrew(crewId);
	}

	private void deleteCrew(Long crewId) {
		crewRepository.deleteById(crewId);
	}

	private int getCrewMemberCnt(Long crewId) {
		return crewMemberRepository.countByCrewId(crewId);
	}

	private void checkMemberExist(Long memberId) {
		if (!memberRepository.existsById(memberId)) {
			throw new NotFoundException("존재하지 않는 회원입니다.");
		}
	}

	private void deleteCrewMember(CrewMember crewMember) {
		crewMemberRepository.deleteById(crewMember.getId());
	}

	private CrewMember findCrewMember(Long crewId, Long memberId) {
		return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).orElseThrow(
			() -> new NotFoundException("크루에 소속된 크루원이 아닙니다.")
		);
	}

	private void processApproval(ProceedApplyReqDto reqDto, Crew findCrew, CrewApplication crewApplication) {
		validateCrewMemberLimit(findCrew);

		Member applyMember = getMemberByMemberId(reqDto.memberId());
		crewApplication.approve();
		CrewMember crewMember = CrewMember.createMember(findCrew, applyMember);
		crewMemberRepository.save(crewMember);

		saveInitialCrewMission(findCrew, applyMember);
	}

	private void validateLeaderPrivilege(Long crewId, Long memberId) {
		CrewMember findCrewMember = getCrewMemberByMemberId(memberId);
		findCrewMember.validateMembership(crewId);
		findCrewMember.checkLeaderPrivilege();
	}

	private void validateCrewMemberLimit(Crew crew) {
		int memberCnt = getCrewMemberCnt(crew.getId());
		if (crew.checkReachedMemberLimit(memberCnt)) {
			throw new ConflictException("최대 인원수를 초과해서 크루원을 받을 수 없습니다.");
		}
	}

	private CrewApplication getCrewApplicationByCrewIdAndMemberId(Long crewId, Long memberId) {
		return crewApplicationRepository.findByCrewIdAndMemberId(crewId, memberId).orElseThrow(
			() -> new NotFoundException("크루에 신청한 사용자가 아닙니다.")
		);
	}

	private CrewMember getCrewMemberByMemberId(Long memberId) {
		return crewMemberRepository.findByMemberId(memberId).orElseThrow(
			() -> new NotFoundException("크루 소속이 아닙니다.")
		);
	}

	private void checkCrewExistById(Long crewId) {
		if (!crewRepository.existsById(crewId)) {
			throw new NotFoundException("크루가 존재하지 않습니다.");
		}
	}

	private List<GetApplyCrewResDto> makeGetApplyDtoList(List<CrewApplication> crewApplicationList) {
		return crewApplicationList.stream()
			.map(GetApplyCrewResDto::new)
			.toList();
	}

	private Crew getCrewByCrewId(Long crewId) {
		return crewRepository.findById(crewId).orElseThrow(
			() -> new NotFoundException("크루를 찾을 수 없습니다.")
		);
	}

	private void validateDuplicateApply(Long crewId, Long memberId) {
		Optional<CrewApplication> crewApplicationOpt = crewApplicationRepository.findByCrewIdAndMemberId(crewId,
			memberId);
		crewApplicationOpt.ifPresent(CrewApplication::checkDuplicateApply);
	}

	private void validateMemberNotInCrew(Long memberId) {
		crewMemberRepository.findByMemberId(memberId).ifPresent(crewMember -> {
			throw new ConflictException("이미 크루에 소속되어 있습니다.");
		});
	}

	private void createAndSaveCrewMember(Crew savedCrew, Member findMember) {
		CrewMember crewLeader = CrewMember.createLeader(savedCrew, findMember);
		crewMemberRepository.save(crewLeader);
	}

	private void checkSubscribedCrew(Long memberId) {
		if (crewMemberRepository.existsByMemberId(memberId)) {
			throw new ConflictException("이미 소속된 크루가 존재합니다.");
		}
	}

	private Member getMemberByMemberId(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(
			() -> new NotFoundException("존재하지 않는 회원입니다.")
		);
	}

	private Crew createAndSaveCrew(CreateCrewReqDto reqDto, Long leaderId) {
		Crew crew = reqDto.toEntity(leaderId);
		return crewRepository.save(crew);
	}

	private void saveTags(List<String> tagNames, Crew savedCrew) {
		if (!tagNames.isEmpty()) {
			List<Tag> tagList = tagNames.stream()
				.map(t -> new Tag(savedCrew, t))
				.toList();
			tagRepository.saveAll(tagList);
		}
	}

	private FindAllCrewResDto convertToFindAllCrewResDto(Crew crew) {
		int memberCnt = getCrewMemberCnt(crew.getId());
		List<String> tags = findTagsToStrings(crew.getId());
		CrewLeaderDto crewLeaderDto = convertCrewLeaderDto(crew.getLeaderId());
		CrewImage crewImage = findImage(crew.getId());
		return FindAllCrewResDto.of(crew, memberCnt, tags, crewLeaderDto, crewImage.getFilepath());
	}

	private CrewLeaderDto convertCrewLeaderDto(Long leaderId) {
		Member crewLeader = getMemberByMemberId(leaderId);
		return new CrewLeaderDto(leaderId, crewLeader.getNickname());
	}

	private List<String> findTagsToStrings(Long crewId) {
		return tagRepository.findAllByCrewId(crewId).stream()
			.map(Tag::getContent)
			.toList();
	}
}
