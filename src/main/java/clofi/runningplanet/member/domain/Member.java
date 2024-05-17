package clofi.runningplanet.member.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseEntity;
import clofi.runningplanet.crew.domain.Crew;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update member set is_delete = true where member_id = ?")
@SQLRestriction("is_delete = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id", nullable = false)
	private Crew crew;

	@Column(name = "nickname", length = 20)
	private String nickname;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "gender", length = 6)
	private Gender gender;

	@Column(name = "age")
	private int age;

	@Column(name = "profile_img", nullable = false)
	private String profileImg;

	@Column(name = "run_score")
	private Integer runScore;

	@Column(name = "avg_pace")
	private Integer avgPace;

	@Column(name = "avg_distance")
	private Integer avgDistance;

	@Column(name = "total_distance", nullable = false)
	private int totalDistance;
}
