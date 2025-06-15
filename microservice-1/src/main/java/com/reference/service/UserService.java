package com.reference.service;

import com.reference.data.bucket.UserBucket;
import com.reference.data.repository.UserRepository;
import com.reference.data.sqs.UserSQS;
import com.reference.exceptions.BucketException;
import com.reference.mapper.UserMapper;
import com.reference.model.UserAPI;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

	private final UserMapper mapper
			= Mappers.getMapper(UserMapper.class);

	private final UserRepository userRepository;

	private final UserBucket userBucket;

	private final UserSQS userSQS;

	public UserAPI getUser(String id) {
		return mapper.domainToAPI(
				mapper.entityToDomain(
						userRepository.getUser(id)));
	}

	public void saveUser(UserAPI user) {
		userRepository.saveUser(mapper.domainToEntity(mapper.apiToDomain(user)));
	}

	public void saveProfilePicture(String id, MultipartFile file) {
		try {
			userBucket.saveProfilePicture(id, file.getBytes());
		} catch (IOException e) {
			throw new BucketException(UserBucket.BUCKET_NAME, id);
		}
	}

	public byte[] getProfilePicture(String id) {
		return userBucket.getProfilePicture(id);
	}

	public void postMessage(String message) {
		userSQS.postMessageToQueue(message);
	}

	public List<String> getMessages() {
		return userSQS.getMessagesFromQueue();
	}
}
