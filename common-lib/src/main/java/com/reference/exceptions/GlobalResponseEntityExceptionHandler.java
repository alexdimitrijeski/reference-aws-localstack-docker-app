package com.reference.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.reference.model.ErrorResponse;

@ControllerAdvice
@Slf4j
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String UNHANDLED_EXCEPTION = "Unexpected error: ";

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(
			Exception ex) {
		log.error(UNHANDLED_EXCEPTION, ex);
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, UNHANDLED_EXCEPTION);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Throwable e){
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(BucketException.class)
	public ResponseEntity<ErrorResponse> handleBucketException(Throwable e){
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private static ResponseEntity<ErrorResponse> buildErrorResponseEntity(
			HttpStatus httpStatus, String message) {
		ErrorResponse error = new ErrorResponse();
		error.setCode(String.valueOf(httpStatus.value()));
		error.setMessage(message);
		return new ResponseEntity<>(
				error, new HttpHeaders(), httpStatus);
	}

}
