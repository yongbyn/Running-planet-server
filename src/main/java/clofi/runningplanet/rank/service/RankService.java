package clofi.runningplanet.rank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import clofi.runningplanet.rank.dto.CrewRankResponse;
import clofi.runningplanet.rank.repository.CrewRankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RankService {
	private final CrewRankRepository crewRankRepository;

	public List<CrewRankResponse> getCrewRankList(String condition, String period) {
		return crewRankRepository.getCrewRank(condition, period);
	}
}
