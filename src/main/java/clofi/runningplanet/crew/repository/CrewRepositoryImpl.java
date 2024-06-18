package clofi.runningplanet.crew.repository;

import static clofi.runningplanet.crew.domain.QCrew.*;
import static java.util.Objects.*;
import static org.springframework.util.ObjectUtils.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import clofi.runningplanet.crew.domain.Category;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.crew.dto.SearchParamDto;
import jakarta.persistence.EntityManager;

public class CrewRepositoryImpl implements CrewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public CrewRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Crew> search(SearchParamDto searchParamDto) {
		return queryFactory
			.select(crew)
			.from(crew)
			.where(crewNameContains(searchParamDto.getCrewName())
				, categoryEq(searchParamDto.getCategory()))
			.fetch();
	}

	private BooleanExpression categoryEq(Category category) {
		return isNull(category) ? null : crew.category.eq(category);
	}

	private BooleanExpression crewNameContains(String crewName) {
		return isEmpty(crewName) ? null : crew.crewName.containsIgnoreCase(crewName);
	}
}
