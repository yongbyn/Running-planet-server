package clofi.runningplanet.crew.repository;

import java.util.List;

import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.SearchParamDto;

public interface CrewRepositoryCustom {
	List<Crew> search(SearchParamDto searchParamDto);
}
