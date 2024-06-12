package clofi.runningplanet.crew.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
import clofi.runningplanet.crew.dto.RuleDto;
import clofi.runningplanet.mission.domain.MissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update crew set deleted_at = now() where crew_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Crew extends BaseSoftDeleteEntity {

	private static final int EXP_MULTIPLIER = 10;
	private static final int MEMBER_INCREMENT = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_id", nullable = false)
	private Long id;

	@Column(name = "leader_id", nullable = false)
	private Long leaderId;

	@Column(name = "crew_name", nullable = false)
	private String crewName;

	@Column(name = "limit_member_cnt", nullable = false)
	private int limitMemberCnt;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 10)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_type", nullable = false, length = 10)
	private ApprovalType approvalType;

	@Column(name = "introduction", length = 4000)
	private String introduction;

	@Column(name = "rule_run_cnt", nullable = false)
	private int ruleRunCnt;

	@Column(name = "rule_distance", nullable = false)
	private int ruleDistance;

	@Column(name = "weekly_distance", nullable = false)
	private int weeklyDistance;

	@Column(name = "total_distance", nullable = false)
	private int totalDistance;

	@Column(name = "crew_exp", nullable = false)
	private int crewExp;

	@Column(name = "crew_level", nullable = false)
	private int crewLevel;

	public Crew(Long leaderId, String crewName, int limitMemberCnt, Category category,
		ApprovalType approvalType,
		String introduction, int ruleRunCnt, int ruleDistance) {
		this(null, leaderId, crewName, limitMemberCnt, category, approvalType, introduction, ruleRunCnt,
			ruleDistance, 0, 0, 0, 1);
	}

	public Crew(Long id, Long leaderId, String crewName, int limitMemberCnt, Category category,
		ApprovalType approvalType, String introduction, int ruleRunCnt, int ruleDistance, int weeklyDistance,
		int totalDistance, int crewExp, int crewLevel) {
		this.id = id;
		this.leaderId = leaderId;
		this.crewName = crewName;
		this.limitMemberCnt = limitMemberCnt;
		this.category = category;
		this.approvalType = approvalType;
		this.introduction = introduction;
		this.ruleRunCnt = ruleRunCnt;
		this.ruleDistance = ruleDistance;
		this.weeklyDistance = weeklyDistance;
		this.totalDistance = totalDistance;
		this.crewExp = crewExp;
		this.crewLevel = crewLevel;
	}

	public boolean checkReachedMemberLimit(int currentMemberCnt) {
		return currentMemberCnt >= limitMemberCnt;
	}

	public void update(ApprovalType approvalType, String introduction, RuleDto rule) {
		this.approvalType = approvalType;
		this.introduction = introduction;
		this.ruleDistance = rule.distance();
		this.ruleRunCnt = rule.weeklyRun();
	}

	public void gainExp(int exp) {
		this.crewExp += exp;
		while (this.crewExp >= getRequiredExp()) {
			levelUp();
		}
	}

	public int getRequiredExp() {
		return limitMemberCnt * MissionType.values().length * EXP_MULTIPLIER * crewLevel;
	}

	private void levelUp() {
		this.crewExp -= getRequiredExp();
		this.crewLevel++;
		this.limitMemberCnt += MEMBER_INCREMENT;
	}
}
