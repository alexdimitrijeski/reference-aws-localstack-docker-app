package com.reference.data.config;

import com.reference.data.lambda.UserLambda;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

/**
 * The type Lambda config.
 * @author adimitrijeski
 */
@Configuration
@RequiredArgsConstructor
public class LambdaConfig {
	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.access.secret-key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.end-point.url}")
	private String awsEndpoint;

	/**
	 * Gets Lambda client.
	 *
	 * @return a Lambda client
	 */
	@Bean
	public LambdaClient getLambdaClient() {
		LambdaClient lambdaClient;
		try {
			LambdaClientBuilder builder = LambdaClient.builder();
			builder.endpointOverride(new URI(awsEndpoint))
					.credentialsProvider(create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)));

			lambdaClient = builder.region(Region.of(awsRegion)).build();
		}  catch(URISyntaxException ex) {
			throw new IllegalStateException("Invalid url: " + awsEndpoint,ex);
		}

		return lambdaClient;
	}

	@Bean
	UserLambda userLambda(){
		return new UserLambda(getLambdaClient());
	}
}
