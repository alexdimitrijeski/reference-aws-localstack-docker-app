package com.reference.data.sqs;

import com.reference.data.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class UserSQS {

	private final SqsClient sqsClient;

	private static final String QUEUE_NAME = UserEntity.ENTITY_NAME +"-queue";

	private static final int WAIT_TIME_IN_SECONDS = 1;
	private static final int MAX_NUMBER_OF_MESSAGES = 10;

	@PostConstruct
	private void postConstruct() {
		createSQSQueue();
	}

	private void createSQSQueue() {
		try {
			Map<QueueAttributeName, String> queueAttributes = new EnumMap<>(QueueAttributeName.class);
			queueAttributes.put(QueueAttributeName.CONTENT_BASED_DEDUPLICATION, "true");

			CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
					.queueName(QUEUE_NAME)
					.attributes(queueAttributes)
					.build();

			sqsClient.createQueue(createQueueRequest);
		} catch (Exception e) {
			log.debug("Error: {}", e.getMessage());
		}
	}

	private String getSqsURL() {
		GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
				.queueName(QUEUE_NAME)
				.build();
		return this.sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();
	}

	public void postMessageToQueue(String message){
		SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
				.messageBody(message)
				.queueUrl(getSqsURL())
				.build();
		sqsClient.sendMessage(sendMessageRequest);
	}

	public List<String> getMessagesFromQueue(){
		List<String> messages = new ArrayList<>();

		ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
				.queueUrl(getSqsURL())
				.waitTimeSeconds(WAIT_TIME_IN_SECONDS)
				.maxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
				.build();

		List<Message> messageList = sqsClient.receiveMessage(receiveMessageRequest).messages();
		for (Message message : messageList){
			messages.add(message.body());
		}

		return messages;
	}
}
