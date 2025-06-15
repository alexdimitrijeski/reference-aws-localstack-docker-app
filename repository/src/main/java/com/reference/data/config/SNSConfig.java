package com.reference.data.config;

import com.reference.data.sns.UserSNS;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

@Configuration
@RequiredArgsConstructor
public class SNSConfig {

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.access.secret-key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.end-point.url}")
	private String awsEndpoint;

	@Bean
	public SnsClient getSNSClient(){
		SnsClient snsClient;

		try{
		SnsClientBuilder builder = SnsClient.builder();
		builder.endpointOverride(new URI(awsEndpoint))
				.credentialsProvider(create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)));

		snsClient = builder.region(Region.of(awsRegion)).build();
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Invalid url: " + awsEndpoint, ex);
		}

		return snsClient;
	}

	@Bean
	UserSNS userSNS(){
		return new UserSNS(getSNSClient());
	}
}
