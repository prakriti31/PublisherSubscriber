package com.example.publishersubscriber.clientprogram;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;

import java.util.List;

public class SubscriberClient {
    public static void main(String[] args) {
        MessageBroker messageBroker = new MessageBroker();
        ClientAPI clientAPI = new ClientAPIImpl(messageBroker);

        // Register subscriber and subscribe to a topic
        String subscriberId = clientAPI.registerSubscriber();
        clientAPI.subscribe(subscriberId, "news");

        // Pull messages from the topic
        List<String> messages = clientAPI.pullMessages(subscriberId, "news");
        for (String message : messages) {
            System.out.println("Received: " + message);
        }
    }
}
