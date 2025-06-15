package com.launch.lambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class BucketHandler implements RequestHandler<S3Event, String> {

	public static final String AWS_REGION_PROPERTY = "aws.region";
	public static final String S3_ENDPOINT_PROPERTY = "aws.end-point.url";
	public static final String ACCESS_KEY_PROPERTY = "aws.access.key";
	public static final String SECRET_KEY_PROPERTY = "aws.access.secret-key";

	public String handleRequest(S3Event s3Event, Context context) {

		// Pull the event records and get the object content type
		String bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
		String key = s3Event.getRecords().get(0).getS3().getObject().getKey();

		S3Object obj = prepareS3().getObject(new GetObjectRequest(bucket, key));
		log.info("S3Object info: {} {}",obj.getKey(), obj.getObjectMetadata().getUserMetadata().toString());

		return obj.getObjectMetadata().getContentType();
	}

	private static AmazonS3 prepareS3() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
			properties.load(resourceStream);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		BasicAWSCredentials credentials = new BasicAWSCredentials(properties.getProperty(ACCESS_KEY_PROPERTY), properties.getProperty(SECRET_KEY_PROPERTY));

		AwsClientBuilder.EndpointConfiguration config =
				new AwsClientBuilder.EndpointConfiguration(properties.getProperty(S3_ENDPOINT_PROPERTY), properties.getProperty(AWS_REGION_PROPERTY));

		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
		builder.withEndpointConfiguration(config);
		builder.withPathStyleAccessEnabled(Boolean.TRUE);
		builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
		return builder.build();
	}
}