package com.reference.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reference.model.UserAPI;
import com.reference.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService mockUserService;

	@Test
	void testCheckHealth() throws Exception {
		// Setup
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/check-health")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("Everything is fine", response.getContentAsString());
	}

	@Test
	void testGetUser() throws Exception {
		// Setup
		// Configure UserService.getUser(...).
		final UserAPI userAPI = new UserAPI();
		userAPI.setId("id");
		userAPI.setUserName("userName");
		userAPI.setCreationUsername("creationUsername");
		userAPI.setCreationDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		userAPI.setLastUpdateUsername("lastUpdateUsername");
		when(mockUserService.getUser("id")).thenReturn(userAPI);

		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/user/{id}", "id")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertTrue(response.getContentAsString().contains("userName"));
	}

	@Test
	void testPutUser() throws Exception {
		// Setup
		final UserAPI user = new UserAPI();
		user.setId("id");
		user.setUserName("userName");
		user.setCreationUsername("creationUsername");
		user.setCreationDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		user.setLastUpdateUsername("lastUpdateUsername");
		user.setLastUpdateDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		user.setVersion("version");
		user.setValidFrom(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		user.setValidTo(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0), ZoneOffset.UTC));
		user.setState("state");

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.excludeFieldsWithoutExposeAnnotation()
				.create();

		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(put("/user/")
						.content(gson.toJson(user)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}

	@Test
	void testPostUserProfilePic() throws Exception {
		// Setup
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(multipart("/user/{id}/profile-pic", "id")
						.file(new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
								"content".getBytes()))
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("Profile pic saved", response.getContentAsString());
		verify(mockUserService).saveProfilePicture(eq("id"), any(MultipartFile.class));
	}

	@Test
	void testGetUserProfilePic() throws Exception {
		// Setup
		when(mockUserService.getProfilePicture("id")).thenReturn("content".getBytes());

		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/user/{id}/profile-pic", "id")
						.accept(MediaType.IMAGE_PNG))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("content", response.getContentAsString());
	}

	@Test
	void testPostMessage() throws Exception {
		// Setup
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(post("/message/")
						.param("message", "message")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("Message sent", response.getContentAsString());
		verify(mockUserService).postMessage("message");
	}

	@Test
	void testGetMessage() throws Exception {
		// Setup
		when(mockUserService.getMessages()).thenReturn(List.of("value"));

		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/message/")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[\"value\"]", response.getContentAsString());
	}

	@Test
	void testGetMessage_UserServiceReturnsNoItems() throws Exception {
		// Setup
		when(mockUserService.getMessages()).thenReturn(Collections.emptyList());

		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/message/")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// Verify the results
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());
	}
}
