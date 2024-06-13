package clofi.runningplanet.common.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3StorageManagerService implements S3StorageManagerUseCase {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${default.imageUrlPrefix}")
	private String imageUrlPrefix;

	@Override
	public List<String> uploadImages(List<MultipartFile> images) {
		return images.stream()
			.filter(this::isValidImage)
			.map(this::uploadImageToS3)
			.collect(Collectors.toList());
	}

	@Override
	public String uploadImage(MultipartFile image) {
		if (!isValidImage(image)) {
			return null;
		}
		return uploadImageToS3(image);
	}

	@Override
	public void deleteImages(String image) {
		amazonS3Client.deleteObject(bucket, image);
	}

	private boolean isValidImage(MultipartFile image) {
		return image != null && !image.isEmpty() && image.getSize() > 0;
	}

	private String uploadImageToS3(MultipartFile image) {
		String imageUrl = generateUniqueFileName(image.getOriginalFilename());
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(image.getContentType());
			metadata.setContentLength(image.getSize());
			amazonS3Client.putObject(
				new PutObjectRequest(bucket, imageUrl, image.getInputStream(), metadata));
		} catch (IOException e) {
			throw new IllegalArgumentException("AWS S3");
		}
		return imageUrlPrefix + imageUrl;
	}

	private String generateUniqueFileName(String originalFilename) {
		int baseLength = UUID.randomUUID().toString().length() + 1;
		int exceedLength = baseLength + originalFilename.length() - 128;

		if (exceedLength > 0) {
			originalFilename = originalFilename.substring(exceedLength);
		}

		return UUID.randomUUID() + "-" + originalFilename;
	}

}
