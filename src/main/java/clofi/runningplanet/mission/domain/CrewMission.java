package clofi.runningplanet.mission.domain;

import clofi.runningplanet.common.domain.BaseEntity;
import clofi.runningplanet.crew.domain.Crew;
import clofi.runningplanet.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@Column(name = "type", nullable = false)
	private MissionType type;

	@Column(name = "is_complete", nullable = false)
	private boolean isCompleted = false;
}
