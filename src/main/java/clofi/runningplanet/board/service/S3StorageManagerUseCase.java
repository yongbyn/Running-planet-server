package clofi.runningplanet.board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3StorageManagerUseCase {
	List<String> uploadImages(List<MultipartFile> images);

	void deleteImages(String image);
}
