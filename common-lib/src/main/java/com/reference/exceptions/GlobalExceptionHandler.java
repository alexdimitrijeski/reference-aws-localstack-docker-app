package com.reference.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error("Unhandled exception caught: " + e.getMessage());
	}
}
