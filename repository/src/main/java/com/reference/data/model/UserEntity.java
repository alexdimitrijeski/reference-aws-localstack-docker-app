package com.reference.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The type User.
 */
@Builder
@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	public static final String ENTITY_NAME = "User";

	private String id;
	private String userName;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("PK")
	public void setId(String id){
		this.id = id;
	}

}
