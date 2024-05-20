package clofi.runningplanet.crew.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.domain.Tag;
import clofi.runningplanet.crew.dto.request.CreateCrewReqDto;
import clofi.runningplanet.crew.repository.CrewRepository;
import clofi.runningplanet.crew.repository.TagRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CrewService {

	private final CrewRepository crewRepository;
	private final TagRepository tagRepository;

	@Transactional
	public Long createCrew(CreateCrewReqDto reqDto) {
		//todo 인증 기능 구현 완료 후 로직 개선
		Crew savedCrew = createAndSaveCrew(reqDto);

		saveTags(reqDto.tags(), savedCrew);

		return savedCrew.getId();
	}

	private Crew createAndSaveCrew(CreateCrewReqDto reqDto) {
		Crew crew = reqDto.toEntity(1L);
		return crewRepository.save(crew);
	}

	private void saveTags(List<String> tagNames, Crew savedCrew) {
		if (tagNames.isEmpty()) {
			return;
		}

		List<Tag> tagList = tagNames.stream()
			.map(t -> new Tag(savedCrew, t))
			.toList();
		tagRepository.saveAll(tagList);
	}
}
