package clofi.runningplanet.rank.repository;

import static clofi.runningplanet.member.domain.QMember.*;
import static clofi.runningplanet.planet.domain.QMemberPlanet.*;
import static clofi.runningplanet.running.domain.QRecord.*;
import static com.querydsl.jpa.JPAExpressions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import clofi.runningplanet.rank.dto.PersonalRankResponse;
import clofi.runningplanet.rank.dto.QPersonalRankResponse;
import clofi.runningplanet.running.domain.QRecord;
import jakarta.persistence.EntityManager;

@Repository
public class PersonalRankRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public PersonalRankRepository(EntityManager em) {
		this.jpaQueryFactory = new JPAQueryFactory(em);
	}

	public List<PersonalRankResponse> getPersonalRank(String condition, String period, LocalDate nowDate) {
		if ("WEEK".equals(period)) {

			LocalDate startOfWeek = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			LocalDate endOfWeek = nowDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

			LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
			LocalDateTime endOfWeekDateTime = endOfWeek.plusDays(1).atStartOfDay().minusSeconds(1);

			QRecord recordSub = new QRecord("recordSub");
			return jpaQueryFactory.select(
					new QPersonalRankResponse(
						member.id,
						member.nickname,
						memberPlanet.countDistinct().intValue(),
						select(recordSub.runDistance.sum().coalesce(0.0).intValue())
							.from(recordSub)
							.where(
								recordSub.member.id.eq(member.id)
									.and(recordSub.createdAt.between(startOfWeekDateTime, endOfWeekDateTime))
							))
				).from(member)
				.leftJoin(memberPlanet).on(
					memberPlanet.memberId.id.eq(member.id)
						.and(memberPlanet.createdAt.between(startOfWeekDateTime, endOfWeekDateTime))
				)
				.leftJoin(record).on(
					record.member.id.eq(member.id)
						.and(record.createdAt.between(startOfWeekDateTime, endOfWeekDateTime))
				)
				.groupBy(member.id)
				.orderBy(sortByCondition(condition, period))
				.fetch();
		} else {
			return jpaQueryFactory.select(
					new QPersonalRankResponse(
						member.id,
						member.nickname,
						memberPlanet.countDistinct().intValue(),
						member.totalDistance.intValue()
					))
				.from(member)
				.leftJoin(memberPlanet).on(memberPlanet.memberId.id.eq(member.id))
				.groupBy(member.id)
				.orderBy(sortByCondition(condition, period))
				.fetch();
		}
	}

	private OrderSpecifier<Integer> sortByCondition(String condition, String period) {
		if ("PLANET".equals(condition)) {
			return memberPlanet.countDistinct().intValue().desc();
		} else if ("WEEK".equals(period) && "DISTANCE".equals(condition)) {
			return record.runDistance.sum().intValue().desc();
		} else {
			return member.totalDistance.intValue().desc();
		}
	}
}
