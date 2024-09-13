package com.example.publishersubscriber.clientAPIlibrary;

import java.util.List;

public interface ClientAPI {
    // Publisher functions
    String registerPublisher();
    void createTopic(String publisherId, String topic);
    void deleteTopic(String publisherId, String topic);
    void sendMessage(String publisherId, String topic, String message);

    // Subscriber functions
    String registerSubscriber();
    void subscribe(String subscriberId, String topic);
    List<String> pullMessages(String subscriberId, String topic);
}
