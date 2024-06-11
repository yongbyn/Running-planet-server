package clofi.runningplanet.planet.domain;

import clofi.runningplanet.common.domain.BaseEntity;
import clofi.runningplanet.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberPlanet extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_planet_id")
	private Long memberPlanetId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "planet_image_id", nullable = false)
	private Planet planetId;

	@Column(name = "member_planet_name", nullable = false, length = 50)
	private String memberPlanetName;

	public MemberPlanet(Member memberId, Planet planetId, String memberPlanetName) {
		this.memberId = memberId;
		this.planetId = planetId;
		this.memberPlanetName = memberPlanetName;
	}

	public void updatePlanetName(String memberPlanetName) {
		this.memberPlanetName = memberPlanetName;
	}
}

