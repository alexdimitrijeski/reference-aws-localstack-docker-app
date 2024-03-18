package com.reference.exceptions;

public class BucketException extends BaseException {

	private static final String MESSAGE = "Error with bucket %s for id %s";

	public BucketException(String bucketName, String entityId){
		super(MESSAGE.formatted(bucketName, entityId));
	}
}
