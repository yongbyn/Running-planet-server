package clofi.runningplanet.mission.domain;

import clofi.runningplanet.common.domain.BaseEntity;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.member.domain.Member;
import clofi.runningplanet.mission.domain.vo.TodayRecords;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CrewMission extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_mission_id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private MissionType type;

	@Column(name = "is_complete", nullable = false)
	private boolean isCompleted = false;

	public CrewMission(Long id, Member member, Crew crew, MissionType type, boolean isCompleted) {
		this.id = id;
		this.member = member;
		this.crew = crew;
		this.type = type;
		this.isCompleted = isCompleted;
	}

	public CrewMission(Member member, Crew crew, MissionType type) {
		this(null, member, crew, type, false);
	}

	public void completeMission() {
		this.isCompleted = true;
	}

	public void validateComplete() {
		if (this.isCompleted()) {
			throw new IllegalArgumentException("이미 완료한 미션입니다.");
		}
	}

	public boolean isMissionComplete(TodayRecords todayRecords) {
		return type.isComplete(todayRecords);
	}

	public double calculateProgress(TodayRecords todayRecords) {
		return Math.min(type.calculateProgress(todayRecords), 1) * 100;
	}
}
