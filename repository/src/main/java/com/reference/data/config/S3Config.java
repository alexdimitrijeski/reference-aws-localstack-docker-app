package com.reference.data.config;

import com.reference.data.bucket.UserBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create;

/**
 * The type S3 config.
 * @author adimitrijeski
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.access.secret-key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.end-point.url}")
	private String awsEndpoint;

	/**
	 * Gets S3 client.
	 *
	 * @return an S3 client
	 */
	@Bean
	public S3Client getS3Client() {
		S3Client s3Client;
		try {
			S3ClientBuilder builder = S3Client.builder();
			builder.endpointOverride(new URI(awsEndpoint))
					.credentialsProvider(create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
					.forcePathStyle(true);
			s3Client = builder.region(Region.of(awsRegion)).build();

		} catch(URISyntaxException ex) {
			throw new IllegalStateException("Invalid url: " + awsEndpoint,ex);
		}

		return s3Client;
	}

	/**
	 * User bucket. This connects to an AWS S3 bucket.
	 *
	 * @return the user bucket
	 */
	@Bean
	UserBucket userBucket(){
		return new UserBucket(getS3Client());
	}

}
