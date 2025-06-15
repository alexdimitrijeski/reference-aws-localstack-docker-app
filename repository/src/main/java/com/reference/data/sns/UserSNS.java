package com.reference.data.sns;

import com.reference.data.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
public class UserSNS {

	private final SnsClient snsClient;

	private static final String TOPIC_NAME = UserEntity.ENTITY_NAME + "-topic";

	@PostConstruct
	private void postConstruct() {
		String topicArn = createSNSTopic(snsClient, TOPIC_NAME);
		log.info(TOPIC_NAME + " topicArn: " + topicArn);
	}

	public static String createSNSTopic(SnsClient snsClient, String topicName) {

		String topicArn = "";
		try {
			CreateTopicRequest request = CreateTopicRequest.builder()
					.name(topicName)
					.build();

			CreateTopicResponse result = snsClient.createTopic(request);

			topicArn = result.topicArn();

			PublishRequest publishRequest = PublishRequest.builder()
					.topicArn(topicArn)
					.subject("Startup Message")
					.message("Startup Message successful").build();

			PublishResponse publishResponse = snsClient.publish(publishRequest);

			log.info("Message sent to topic. Message ID: " + publishResponse.messageId());
		} catch (SnsException e) {
			log.error(e.awsErrorDetails().errorMessage());
		}
		return topicArn;
	}

}
