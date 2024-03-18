package com.reference.data.lambda;

import com.reference.data.bucket.UserBucket;
import com.reference.exceptions.LambdaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.ResourceConflictException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import software.amazon.awssdk.services.lambda.model.Runtime;

/**
 * The type User lambda.
 */
@Slf4j
@RequiredArgsConstructor
public class UserLambda {

	private final LambdaClient lambdaClient;

	@PostConstruct
	private void postConstruct() {
		createLambda();
	}

	private void createLambda() {
		File file = new File("../lambda-example/build/distributions/lambda-example-0.0.1-SNAPSHOT.zip");

		try (InputStream inputStream = new FileInputStream(file)) {
			SdkBytes sdkBytes = SdkBytes.fromInputStream(inputStream);

			CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
					.functionName("client-lambda-user")
					.runtime(Runtime.JAVA11)
					.code(f ->
									f.s3Bucket(UserBucket.BUCKET_NAME)
											.zipFile(sdkBytes)
					)
					.handler("com.launch.lambda.BucketHandler")
					.role("arn:aws:iam::123456789012:role/ignoreme")
					.build();
			lambdaClient.createFunction(createFunctionRequest);
		} catch (ResourceConflictException e) {
			log.info("Error: {}", e.getMessage());
		} catch (FileNotFoundException e) {
			throw new LambdaException("Lambda file for example not found. Please run task buildZip under lambda-example module");
		} catch (Exception e) {
			throw new LambdaException(e.getMessage());
		}
	}
}
