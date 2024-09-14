package com.example.publishersubscriber.clientprogram;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;

import java.util.List;

public class SubscriberClient {
    private final ClientAPI clientAPI;

    // Constructor that accepts an external MessageBroker
    public SubscriberClient(MessageBroker messageBroker) {
        this.clientAPI = new ClientAPIImpl(messageBroker);
    }

    public void runSubscriber(String topic) {
        // Register subscriber and subscribe to a topic
        String subscriberId = clientAPI.registerNewSubscriber();
        clientAPI.subscribeToTopic(subscriberId, topic);

        // Pull messages from the topic
        List<String> messages = clientAPI.pullMessagesFromPool(subscriberId, topic);
        for (String message : messages) {
            System.out.println("Subscriber " + subscriberId + " received: " + message);
        }
    }
}
