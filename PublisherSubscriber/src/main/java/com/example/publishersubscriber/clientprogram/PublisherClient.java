package com.example.publishersubscriber.clientprogram;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.springframework.http.converter.json.GsonBuilderUtils;

public class PublisherClient {
    private final ClientAPI clientAPI;

    // Constructor that accepts an external MessageBroker
    public PublisherClient(MessageBroker messageBroker) {
        this.clientAPI = new ClientAPIImpl(messageBroker);
    }

    public void executePublisher(String message1, String message2) {
        // Register publisher and create a topic
        String publisherId = clientAPI.registerNewPublisher();
        clientAPI.createNewTopicToPublisher(publisherId, "education");

        // Send messages to the topic
        clientAPI.sendMessageToTopic(publisherId, "education", message1);
        clientAPI.sendMessageToTopic(publisherId, "education", message2);
        System.out.println("Messages sent by publisher: " + publisherId);
    }
}
