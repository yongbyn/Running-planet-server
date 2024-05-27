package clofi.runningplanet.crew.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.CrewApplication;
import clofi.runningplanet.crew.domain.CrewMember;
import clofi.runningplanet.crew.domain.Tag;
import clofi.runningplanet.crew.dto.CrewLeaderDto;
import clofi.runningplanet.crew.dto.request.ApplyCrewReqDto;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.dto.response.ApplyCrewResDto;
import clofi.runningplanet.crew.dto.response.FindAllCrewResDto;
import clofi.runningplanet.crew.dto.response.FindCrewResDto;
import clofi.runningplanet.crew.repository.CrewApplicationRepository;
import clofi.runningplanet.crew.repository.CrewMemberRepository;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.repository.TagRepository;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CrewService {

	private final CrewRepository crewRepository;
	private final TagRepository tagRepository;
	private final MemberRepository memberRepository;
	private final CrewApplicationRepository crewApplicationRepository;
	private final CrewMemberRepository crewMemberRepository;

	@Transactional
	public Long createCrew(CreateCrewReqDto reqDto, Long memberId) {
		Member findMember = getMemberByMemberId(memberId);
		checkSubscribedCrew(memberId);

		Crew savedCrew = createAndSaveCrew(reqDto, memberId);
		saveTags(reqDto.tags(), savedCrew);

		createAndSaveCrewMember(savedCrew, findMember);
		return savedCrew.getId();
	}

	@Transactional(readOnly = true)
	public List<FindAllCrewResDto> findAllCrew() {
		List<Crew> crewList = crewRepository.findAll();

		return crewList.stream()
			.map(this::convertToFindAllCrewResDto)
			.toList();
	}

	@Transactional(readOnly = true)
	public FindCrewResDto findCrew(Long crewId) {
		Crew findCrew = getByCrewId(crewId);

		List<String> tags = findTagsToStrings(findCrew.getId());
		CrewLeaderDto crewLeader = convertCrewLeaderDto(findCrew.getLeaderId());

		return FindCrewResDto.of(findCrew, crewLeader, tags);
	}

	@Transactional
	public ApplyCrewResDto applyCrew(ApplyCrewReqDto reqDto, Long crewId, Long memberId) {

		Member findMember = getMemberByMemberId(memberId);

		validateMemberNotInCrew(findMember.getId());
		validateCrewApplicationNotExist(crewId, findMember.getId());

		Crew findCrew = getByCrewId(crewId);

		findCrew.checkRunScore(findMember.getRunScore());

		CrewApplication crewApplication = reqDto.toEntity(findCrew, findMember);
		crewApplicationRepository.save(crewApplication);
		return new ApplyCrewResDto(crewId, findMember.getId(), true);
	}

	private Crew getByCrewId(Long crewId) {
		return crewRepository.findById(crewId).orElseThrow(
			() -> new NotFoundException("크루를 찾을 수 없습니다.")
		);
	}

	private void validateCrewApplicationNotExist(Long crewId, Long memberId) {
		if (crewApplicationRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent()) {
			throw new ConflictException("이미 신청한 크루입니다.");
		}
	}

	private void validateMemberNotInCrew(Long memberId) {
		if (crewMemberRepository.findByMemberId(memberId).isPresent()) {
			throw new ConflictException("이미 크루에 소속되어 있습니다.");
		}
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
		List<String> tags = findTagsToStrings(crew.getId());
		CrewLeaderDto crewLeaderDto = convertCrewLeaderDto(crew.getLeaderId());
		return FindAllCrewResDto.of(crew, tags, crewLeaderDto);
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
