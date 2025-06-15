package com.reference.data.config;

import com.reference.data.sqs.UserSQS;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

@Configuration
@RequiredArgsConstructor
public class SQSConfig {

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.access.secret-key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.end-point.url}")
	private String awsEndpoint;

	@Bean
	public SqsClient getSQSClient(){
		SqsClient sqsClient;

		try{
		SqsClientBuilder builder = SqsClient.builder();
		builder.endpointOverride(new URI(awsEndpoint))
				.credentialsProvider(create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)));

		sqsClient = builder.region(Region.of(awsRegion)).build();
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Invalid url: " + awsEndpoint, ex);
		}

		return sqsClient;
	}

	@Bean
	UserSQS userSQS(){
		return new UserSQS(getSQSClient());
	}
}
