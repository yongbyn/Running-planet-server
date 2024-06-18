package clofi.runningplanet.crew.dto;

import clofi.runningplanet.crew.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SearchParamDto {

	private String crewName;
	private Category category;

	public SearchParamDto(String crewName, String category) {
		this.crewName = crewName;
		this.category = parseCategory(category);
	}

	private Category parseCategory(String category) {
		if (category != null && !category.trim().isEmpty()) {
			try {
				return Category.valueOf(category.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}
}
