package com.reference.data.bucket;

import com.reference.data.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * The User bucket.
 */
@Slf4j
@RequiredArgsConstructor
public class UserBucket {
	private final S3Client s3Client;

	private static final String PROFILE_PICTURE_KEY_PREFIX = "profile-picture-";

	public static final String BUCKET_NAME = UserEntity.ENTITY_NAME.toLowerCase(Locale.ROOT);

	@PostConstruct
	private void postConstruct() {
		createBucket();
	}

	private void createBucket() {
		try {
			CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
					.bucket(BUCKET_NAME)
					.build();
			s3Client.createBucket(createBucketRequest);
		} catch (BucketAlreadyOwnedByYouException e) {
			log.debug("Error: {}", e.getMessage());
		}
	}

	/**
	 * Put the supplied object in the User bucket for the given key.
	 *
	 * @param key         the key
	 * @param requestBody the request body
	 */
	private void putObject(String key, RequestBody requestBody) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(key)
				.build();

		s3Client.putObject(putObjectRequest, requestBody);
	}

	private byte[] getObject(String key) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(key)
				.build();

		ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
		return objectBytes.asByteArray();
	}

	public void saveProfilePicture(String id, byte[] bytesArray) {
		String key = PROFILE_PICTURE_KEY_PREFIX + id;
		putObject(key, RequestBody.fromBytes(bytesArray));
	}

	public byte[] getProfilePicture(String id) {
		String key = PROFILE_PICTURE_KEY_PREFIX + id;
		return getObject(key);
	}
}

