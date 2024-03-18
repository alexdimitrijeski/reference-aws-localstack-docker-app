package com.reference.exceptions;

public class EntityNotFoundException extends BaseException {

	private static final String MESSAGE = "%s not found for id %s";

	public EntityNotFoundException(String tableName, String entityId){
		super(MESSAGE.formatted(tableName, entityId));
	}
}
