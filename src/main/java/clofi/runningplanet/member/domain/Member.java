package clofi.runningplanet.member.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update member set deleted_at = now() where member_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseSoftDeleteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", nullable = false)
	private Long id;

	@Column(name = "nickname", length = 20)
	private String nickname;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "gender", length = 6)
	private Gender gender;

	@Column(name = "age")
	private Integer age;

	@Column(name = "weight")
	private Integer weight;

	@Column(name = "profile_img", nullable = false)
	private String profileImg;

	@Column(name = "exp", columnDefinition = "double default 0.0")
	private double exp;

	@Column(name = "avg_pace")
	private int avgPace;

	@Column(name = "avg_distance")
	private double avgDistance;

	@Column(name = "total_distance", nullable = false)
	private double totalDistance;

	@Builder
	public Member(Long id, String nickname, Gender gender, Integer age, Integer weight, String profileImg,
		double exp,
		int avgPace,
		int avgDistance, int totalDistance) {
		this.id = id;
		this.nickname = nickname;
		this.gender = gender;
		this.age = age;
		this.weight = weight;
		this.profileImg = profileImg;
		this.exp = exp;
		this.avgPace = avgPace;
		this.avgDistance = avgDistance;
		this.totalDistance = totalDistance;
	}

	public void update(String nickname, int weight, Gender gender, int age, String profileImg) {
		this.nickname = nickname;
		this.weight = weight;
		this.gender = gender;
		this.age = age;
		this.profileImg = profileImg;
	}

	public void onboarding(Gender gender, Integer age, Integer weight) {
		this.gender = gender;
		this.age = age;
		this.weight = weight;
	}

	public void updateRunningStatistics(int totalRunTime, double totalRunDistance, int recordCount) {
		if (totalRunTime == 0 || recordCount == 0 || totalRunDistance == 0) {
			return;
		}
		this.avgDistance = totalRunDistance / recordCount;
		this.totalDistance = totalRunDistance;
		this.avgPace = (int)(totalRunTime / totalRunDistance);
	}

	public void increaseExp(double runDistance) {
		this.exp += runDistance;
	}
}
