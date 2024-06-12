package clofi.runningplanet.crew.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import clofi.runningplanet.common.domain.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SQLDelete(sql = "update crew set deleted_at = now() where crew_image_id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CrewImage extends BaseSoftDeleteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_image_id", nullable = false)
	private Long id;

	@Column(name = "original_filename", nullable = false)
	private String originalFilename;

	@Column(name = "filepath", unique = true, nullable = false)
	private String filepath;

	@OneToOne
	@JoinColumn(name = "crew_id")
	private Crew crew;

	public CrewImage(String originalFilename, String filepath, Crew crew) {
		this(null, originalFilename, filepath, crew);
	}

	public CrewImage(Long id, String originalFilename, String filepath, Crew crew) {
		this.id = id;
		this.originalFilename = originalFilename;
		this.filepath = filepath;
		this.crew = crew;
	}

	public void update(String filepath, String originalFilename) {
		this.filepath = filepath;
		this.originalFilename = originalFilename;
	}
}
