package com.reference.data.repository;

import com.reference.data.model.UserEntity;
import com.reference.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import javax.annotation.PostConstruct;

import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;

/**
 * The type User repository.
 */
@Slf4j
@RequiredArgsConstructor
public class UserRepository {

	private final DynamoDbEnhancedClient dbEnhancedClient;

	private DynamoDbTable<UserEntity> userTable;

	@PostConstruct
	private void postConstruct() {
		createTable();
		prePopulate();
	}

	private void createTable(){
		try {
			userTable = dbEnhancedClient.table(UserEntity.ENTITY_NAME, fromBean(UserEntity.class));
			userTable.createTable();
		} catch (ResourceInUseException e){
			log.debug("Error: {}", e.getMessage());
		}
	}

	private void prePopulate(){
		userTable.putItem(UserEntity.builder().id("1").userName("user1").build());
		userTable.putItem(UserEntity.builder().id("2").userName("user2").build());
	}

	/**
	 * Save user.
	 *
	 * @param user the user
	 */
	public void saveUser(UserEntity user){
		userTable.putItem(user);
	}

	public UserEntity getUser(String id){
		UserEntity user =  userTable.getItem(Key.builder().partitionValue(id).build());
		if(user != null) {
			return user;
		} else {
			throw new EntityNotFoundException(UserEntity.ENTITY_NAME, id);
		}
	}
}
