package clofi.runningplanet.rank.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import clofi.runningplanet.rank.dto.CrewRankResponse;
import clofi.runningplanet.rank.dto.PersonalRankResponse;
import clofi.runningplanet.rank.repository.CrewRankRepository;
import clofi.runningplanet.rank.repository.PersonalRankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RankService {
	private final CrewRankRepository crewRankRepository;
	private final PersonalRankRepository personalRankRepository;

	public List<CrewRankResponse> getCrewRankList(String condition, String period) {
		return crewRankRepository.getCrewRank(condition, period);
	}

	public List<PersonalRankResponse> getPersonalRankList(String condition, String period, LocalDate nowDate) {
		return personalRankRepository.getPersonalRank(condition, period, nowDate);
	}
}
