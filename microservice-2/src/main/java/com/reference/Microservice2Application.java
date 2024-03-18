package com.reference;

import com.reference.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Microservice2Application {

	public static void main(String[] args) {
		GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
		SpringApplication.run(Microservice2Application.class, args);
	}
}