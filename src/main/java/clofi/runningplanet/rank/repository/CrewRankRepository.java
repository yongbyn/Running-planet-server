package clofi.runningplanet.rank.repository;

import static clofi.runningplanet.crew.domain.QCrew.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import clofi.runningplanet.rank.dto.CrewRankResponse;
import clofi.runningplanet.rank.dto.QCrewRankResponse;
import jakarta.persistence.EntityManager;

@Repository
public class CrewRankRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public CrewRankRepository(EntityManager em) {
		this.jpaQueryFactory = new JPAQueryFactory(em);
	}

	public List<CrewRankResponse> getCrewRank(String condition, String period) {
		if (period.equals("WEEK")) {
			return jpaQueryFactory.select(
					new QCrewRankResponse(
						crew.id,
						crew.crewName,
						crew.crewLevel,
						crew.weeklyDistance
					)
				).from(crew)
				.orderBy(sortByCondition(condition))
				.fetch();
		} else {
			return jpaQueryFactory.select(
					new QCrewRankResponse(
						crew.id,
						crew.crewName,
						crew.crewLevel,
						crew.totalDistance
					)
				).from(crew)
				.orderBy(sortByCondition(condition))
				.fetch();
		}
	}

	private OrderSpecifier<Integer> sortByCondition(String condition) {
		if (condition.equals("LEVEL")) {
			return crew.crewLevel.desc();
		} else {
			return crew.totalDistance.desc();
		}
	}
}
