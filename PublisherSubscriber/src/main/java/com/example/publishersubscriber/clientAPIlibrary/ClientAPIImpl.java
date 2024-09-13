package com.example.publishersubscriber.clientAPIlibrary;

import com.example.publishersubscriber.serverprogram.MessageBroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientAPIImpl implements ClientAPI {

    private final MessageBroker messageBroker;

    public ClientAPIImpl(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    // Register Publisher
    @Override
    public String registerPublisher() {
        return messageBroker.registerPublisher();
    }

    // Create Topic
    @Override
    public void createTopic(String publisherId, String topic) {
        messageBroker.createTopic(publisherId, topic);
    }

    // Delete Topic
    @Override
    public void deleteTopic(String publisherId, String topic) {
        messageBroker.deleteTopic(publisherId, topic);
    }

    // Send Message
    @Override
    public void sendMessage(String publisherId, String topic, String message) {
        messageBroker.sendMessage(publisherId, topic, message);
    }

    // Register Subscriber
    @Override
    public String registerSubscriber() {
        return messageBroker.registerSubscriber();
    }

    // Subscribe to Topic
    @Override
    public void subscribe(String subscriberId, String topic) {
        messageBroker.subscribe(subscriberId, topic);
    }

    // Pull Messages from Topic
    @Override
    public List<String> pullMessages(String subscriberId, String topic) {
        return messageBroker.pullMessages(subscriberId, topic);
    }
}
