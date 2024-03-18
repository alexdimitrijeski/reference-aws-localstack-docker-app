package com.reference.controller;

import com.reference.model.UserAPI;
import com.reference.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * The Microservice 1 controller.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * Check health of microservice
	 *
	 * @return status of microservice
	 */
	@GetMapping("/check-health")
	public ResponseEntity<String> checkHealth() {
		log.info("entering checkHealth");
		final ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.OK);
		log.info("leaving checkHealth");
		return bodyBuilder.body("Everything is fine");
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<UserAPI> getUser(@PathVariable("id") String id) {
		return ResponseEntity.ofNullable(userService.getUser(id));
	}

	@PutMapping(value = "/user/", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public void putUser(@RequestBody UserAPI user) {
		userService.saveUser(user);
	}

	@PostMapping("/user/{id}/profile-pic")
	public ResponseEntity<String> postUserProfilePic(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
		userService.saveProfilePicture(id, file);
		return ResponseEntity.ok().body("Profile pic saved");
	}

	@GetMapping(value = "/user/{id}/profile-pic", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getUserProfilePic(@PathVariable("id") String id){
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(userService.getProfilePicture(id));
	}

	@PostMapping("/message/")
	public ResponseEntity<String> postMessage(@RequestParam("message") String message){
		userService.postMessage(message);
		return ResponseEntity.ok().body("Message sent");
	}

	@GetMapping("/message/")
	public ResponseEntity<List<String>> getMessage(){
		return ResponseEntity.ok().body(userService.getMessages());
	}
}
