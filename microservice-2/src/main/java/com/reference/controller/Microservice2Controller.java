package com.reference.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Microservice 2 controller.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class Microservice2Controller {


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
}
