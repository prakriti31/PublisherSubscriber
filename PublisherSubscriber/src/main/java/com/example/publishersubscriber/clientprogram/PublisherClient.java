package com.example.publishersubscriber.clientprogram;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.GsonBuilderUtils;

public class PublisherClient {
    private static final Logger log = LoggerFactory.getLogger(PublisherClient.class);
    private final ClientAPI clientAPI;

    // Constructor that accepts an external MessageBroker
    public PublisherClient(MessageBroker messageBroker) {
        this.clientAPI = new ClientAPIImpl(messageBroker);
    }

    // Function to execute publisher
    public void executePublisher(String message1, String message2) {
        // Register publisher and create a topic
        String publisherId = clientAPI.registerNewPublisher();
        System.out.println("Newly registered Publisher ID: " + publisherId);
        // Create new topic under the publisher
        clientAPI.createNewTopicToPublisher(publisherId, "education");
        System.out.println("Created a topic successfully for " + publisherId);
        // Send messages to the topic
        clientAPI.sendMessageToTopic(publisherId, "education", message1);
        clientAPI.sendMessageToTopic(publisherId, "education", message2);
        System.out.println("Messages published by publisher: " + publisherId);
        System.out.println("********** The messages lie in the pool *********");
    }
}
