package com.reference.service;

import com.reference.data.bucket.UserBucket;
import com.reference.data.model.UserEntity;
import com.reference.data.repository.UserRepository;
import com.reference.data.sqs.UserSQS;
import com.reference.exceptions.BucketException;
import com.reference.mapper.UserMapper;
import com.reference.model.UserAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {

	@Mock
	private UserRepository mockUserRepository;
	@Mock
	private UserBucket mockUserBucket;
	@Mock
	private UserSQS mockUserSQS;
	@Mock
	private UserMapper mockMapper;

	private UserService userServiceUnderTest;

	private AutoCloseable mockitoCloseable;

	@BeforeEach
	void setUp() {
		mockitoCloseable = openMocks(this);
		userServiceUnderTest = new UserService(mockUserRepository, mockUserBucket, mockUserSQS);
	}

	@AfterEach
	void tearDown() throws Exception {
		mockitoCloseable.close();
	}

	@Test
	void testGetUser() {
		// Setup
		final UserAPI expectedResult = new UserAPI();
		expectedResult.setId("id");
		expectedResult.setUserName("userName");
		expectedResult.setCreationUsername("creationUsername");
		expectedResult.setCreationDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		expectedResult.setLastUpdateUsername("lastUpdateUsername");

		final UserEntity userEntity = UserEntity.builder()
				.id("id")
				.userName("userName")
				.build();

		when(mockUserRepository.getUser("id")).thenReturn(userEntity);
		when(mockMapper.domainToAPI(any())).thenReturn(expectedResult);

		// Run the test
		final UserAPI result = userServiceUnderTest.getUser("id");

		// Verify the results
		assertEquals(expectedResult.getId(), result.getId());
		assertEquals(expectedResult.getUserName(), result.getUserName());
	}

	@Test
	void testSaveUser() {
		// Setup
		final UserAPI user = new UserAPI();
		user.setId("id");
		user.setUserName("userName");
		user.setCreationUsername("creationUsername");
		user.setCreationDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		user.setLastUpdateUsername("lastUpdateUsername");

		UserEntity userEntity = UserEntity.builder()
				.id("id")
				.userName("userName")
				.build();

		// Run the test
		userServiceUnderTest.saveUser(user);

		// Verify the results
		verify(mockUserRepository).saveUser(userEntity);
	}

	@Test
	void testSaveProfilePicture() throws IOException {
		// Setup
		final MultipartFile mockFile = mock(MultipartFile.class);

		// Run the test
		userServiceUnderTest.saveProfilePicture("id", mockFile);

		// Verify the results
		verify(mockUserBucket).saveProfilePicture(eq("id"), any());
	}

	@Test
	void testSaveProfilePicture_ThrowsException() throws IOException {
		// Setup
		MultipartFile mockFile = mock(MultipartFile.class);
		when(mockFile.getBytes()).thenThrow(new IOException());

		// Run the test and verify
		assertThrows(BucketException.class, ()->userServiceUnderTest.saveProfilePicture("id", mockFile));
	}

	@Test
	void testGetProfilePicture() {
		// Setup
		when(mockUserBucket.getProfilePicture("id")).thenReturn("content".getBytes());

		// Run the test
		final byte[] result = userServiceUnderTest.getProfilePicture("id");

		// Verify the results
		assertArrayEquals("content".getBytes(), result);
	}

	@Test
	void testPostMessage() {
		// Setup
		// Run the test
		userServiceUnderTest.postMessage("message");

		// Verify the results
		verify(mockUserSQS).postMessageToQueue("message");
	}

	@Test
	void testGetMessages() {
		// Setup
		when(mockUserSQS.getMessagesFromQueue()).thenReturn(List.of("value"));

		// Run the test
		final List<String> result = userServiceUnderTest.getMessages();

		// Verify the results
		assertEquals(List.of("value"), result);
	}

	@Test
	void testGetMessages_UserSQSReturnsNoItems() {
		// Setup
		when(mockUserSQS.getMessagesFromQueue()).thenReturn(Collections.emptyList());

		// Run the test
		final List<String> result = userServiceUnderTest.getMessages();

		// Verify the results
		assertEquals(Collections.emptyList(), result);
	}
}
