package clofi.runningplanet.common.utils;

import org.springframework.beans.factory.annotation.Value;

public abstract class ImageUrlFormatter {

	@Value("${default.imageUrlPrefix}")
	private static String imageUrlPrefix;

	public static String checkImageUrl(String ImageUrl) {
		if (!ImageUrl.startsWith("http")) {
			return imageUrlPrefix + ImageUrl;
		}
		return ImageUrl;
	}
}
