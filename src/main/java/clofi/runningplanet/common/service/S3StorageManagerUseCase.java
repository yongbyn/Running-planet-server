package clofi.runningplanet.common.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3StorageManagerUseCase {
	List<String> uploadImages(List<MultipartFile> images);

	String uploadImage(MultipartFile image);

	void deleteImages(String image);
}
