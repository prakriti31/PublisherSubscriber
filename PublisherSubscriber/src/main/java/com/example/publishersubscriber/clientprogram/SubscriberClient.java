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

    // Function to run subscriber
    public void runSubscriber(String topic) {
        // Register subscriber and subscribeTopic to a topic
        String subscriberId = clientAPI.registerNewSubscriber();
        // Calling subscribe to topic
        clientAPI.subscribeToTopic(subscriberId, topic);
        System.out.println("Subscriber " + subscriberId + " subscribed to topic : " + topic);
        // Pull messages from the topic
        // Storing the data in a List by calling pull messages from pool function
        List<String> messages = clientAPI.pullMessagesFromPool(subscriberId, topic);
        for (String message : messages) {
            System.out.println("Subscriber " + subscriberId + " received: " + message);
        }
    }
}
