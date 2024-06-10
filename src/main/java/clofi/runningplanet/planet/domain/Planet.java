package clofi.runningplanet.planet.domain;

import clofi.runningplanet.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Planet extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "planet_image_id")
	private Long planetImageId;

	@Column(name = "first_planet", nullable = false, length = 255)
	private String firstPlanet;

	@Column(name = "second_planet", nullable = false, length = 255)
	private String secondPlanet;

	@Column(name = "third_planet", nullable = false, length = 255)
	private String thirdPlanet;

	@Column(name = "fourth_planet", nullable = false, length = 255)
	private String fourthPlanet;

	@Column(name = "fifth_planet", nullable = false, length = 255)
	private String fifthPlanet;

	@Column(name = "planet_default_name", nullable = false, length = 50)
	private String planetDefaultName;

	public Planet(String firstPlanet, String secondPlanet, String thirdPlanet, String fourthPlanet, String fifthPlanet,
		String planetDefaultName) {
		this.firstPlanet = firstPlanet;
		this.secondPlanet = secondPlanet;
		this.thirdPlanet = thirdPlanet;
		this.fourthPlanet = fourthPlanet;
		this.fifthPlanet = fifthPlanet;
		this.planetDefaultName = planetDefaultName;
	}
}
