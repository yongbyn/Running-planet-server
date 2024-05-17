package clofi.runningplanet.crew.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseEntity;
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
@SQLDelete(sql = "update crew set is_delete = true where crew = ?")
@SQLRestriction("is_delete = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Crew extends BaseEntity {
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
}
