package com.example.publishersubscriber.clientprogram;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;

public class PublisherClient {
    public static void main(String[] args) {
        MessageBroker messageBroker = new MessageBroker();
        ClientAPI clientAPI = new ClientAPIImpl(messageBroker);

        // Register publisher and create a topic
        String publisherId = clientAPI.registerPublisher();
        clientAPI.createTopic(publisherId, "news");

        // Send messages to the topic
        clientAPI.sendMessage(publisherId, "news", "Breaking news 1");
        clientAPI.sendMessage(publisherId, "news", "Breaking news 2");
    }
}
