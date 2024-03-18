package com.reference.data.config;

import com.reference.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

/**
 * The type Dynamo db config.
 *
 * @author adimitrijeski
 */
@Configuration
@RequiredArgsConstructor
public class DynamoDbConfig {

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.access.secret-key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.end-point.url}")
	private String awsEndpoint;

	/**
	 * Gets dynamo db client.
	 *
	 * @return the dynamo db client
	 */
	@Bean
	public DynamoDbClient getDynamoDbClient() {
		DynamoDbClient dynamoDB;
		try {
			DynamoDbClientBuilder builder = DynamoDbClient.builder();
			builder.endpointOverride(new URI(awsEndpoint))
					.credentialsProvider(create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)));

			dynamoDB = builder.region(Region.of(awsRegion)).build();
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Invalid url: " + awsEndpoint, ex);
		}
		return dynamoDB;
	}

	/**
	 * Gets dynamo db enhanced client.
	 *
	 * @return the dynamo db enhanced client
	 */
	@Bean
	public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
		return DynamoDbEnhancedClient.builder()
				.dynamoDbClient(getDynamoDbClient())
				.build();
	}

	/**
	 * User Repository. This connects to a DynamoDB table.
	 *
	 * @return the user repository
	 */
	@Bean
	UserRepository userRepository() {
		return new UserRepository(getDynamoDbEnhancedClient());
	}

}
