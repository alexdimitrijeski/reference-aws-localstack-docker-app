package com.reference;

import com.reference.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Microservice1Application {

	public static void main(String[] args) {
		GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
		SpringApplication.run(Microservice1Application.class, args);
	}
}