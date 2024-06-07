package clofi.runningplanet.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import clofi.runningplanet.common.service.S3StorageManagerUseCase;

public class FakeS3StorageManager implements S3StorageManagerUseCase {
	@Override
	public List<String> uploadImages(List<MultipartFile> images) {
		return Arrays.asList("fakeImageUrl1", "fakeImageUrl2");
	}

	@Override
	public String uploadImage(MultipartFile image) {
		return "https://fakeImageUrl";
	}

	@Override
	public void deleteImages(String image) {

	}
}
