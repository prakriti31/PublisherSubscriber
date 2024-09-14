package com.example.publishersubscriber.clientAPIlibrary;

import java.util.List;

public interface ClientAPI {

    // This file creates an interface for the functions
    // Publisher Client or the Subscriber Client (might or might not) utilize

    // Publisher functions
    // This function registers a new publisher and returns the new publisherID
    String registerNewPublisher();
    // This function creates a new topic under a specific publisherID
    void createNewTopicToPublisher(String publisherId, String topic);
    // This function deletes a specific topic from the mentioned publisherID
    void deleteTopicFromPublisher(String publisherId, String topic);
    // This function sends a message to the specified topic from the registered publisherID
    void sendMessageToTopic(String publisherId, String topic, String message);

    // Subscriber functions
    // This function registers a new subscriber and returns the new subscriberID
    String registerNewSubscriber();
    // This function subscribes to the topics
    void subscribeToTopic(String subscriberId, String topic);
    // This function pulls all the unread messages from the subscribed
    List<String> pullMessagesFromPool(String subscriberId, String topic);

    // Helping Functions
    // Update topic name

    // Fetches the messages from mentioned multiple topics at the same time
    // Fetches the messages from all subscribed topics
    // Fetches all topics registered under the specified PublisherID

    // Get the status of read flag for a message
    // Fetch all messages for a topic(read and unread)

}
