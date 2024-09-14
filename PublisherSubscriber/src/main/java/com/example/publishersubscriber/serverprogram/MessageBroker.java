package com.example.publishersubscriber.serverprogram;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageBroker {
    // Data structures to hold topics and messages
    private final Map<String, List<String>> topicMessages = new HashMap<>();
    private final Map<String, Set<String>> topicSubscribers = new HashMap<>();
    private final Map<String, Set<String>> subscriberMessagesRead = new HashMap<>();

    private int publisherIdCounter = 1;
    private int subscriberIdCounter = 1;

    // Register Publisher
    public String registerNewPublisher() {
        return "PUB-" + publisherIdCounter++;
    }

    // Create Topic
    public void createNewTopicToPublisher(String publisherId, String topic) {
        if (!topicMessages.containsKey(topic)) {
            topicMessages.put(topic, new ArrayList<>());
            topicSubscribers.put(topic, new HashSet<>());
        }
    }

    // Delete Topic
    public void deleteTopicFromPublisher(String publisherId, String topic) {
        topicMessages.remove(topic);
        topicSubscribers.remove(topic);
    }

    // Send Message to Topic
    public void sendMessageToTopic(String publisherId, String topic, String message) {
        List<String> messages = topicMessages.getOrDefault(topic, new ArrayList<>());
        messages.add(message);
        topicMessages.put(topic, messages);
    }

    // Register Subscriber
    public String registerSubscriber() {
        String subscriberId = "SUB-" + subscriberIdCounter++;
        subscriberMessagesRead.put(subscriberId, new HashSet<>());
        return subscriberId;
    }

    // Subscribe to a Topic
    public void subscribeToTopic(String subscriberId, String topic) {
        Set<String> subscribers = topicSubscribers.getOrDefault(topic, new HashSet<>());
        subscribers.add(subscriberId);
        topicSubscribers.put(topic, subscribers);
    }

    // Pull Messages from Topic
    public List<String> pullMessagesFromPool(String subscriberId, String topic) {
        List<String> messages = new ArrayList<>(topicMessages.getOrDefault(topic, new ArrayList<>()));
        Set<String> messagesRead = subscriberMessagesRead.getOrDefault(subscriberId, new HashSet<>());

        List<String> unreadMessages = new ArrayList<>();
        for (String message : messages) {
            if (!messagesRead.contains(message)) {
                unreadMessages.add(message);
                messagesRead.add(message);
            }
        }

        subscriberMessagesRead.put(subscriberId, messagesRead);

        // Remove messages that have been read by all subscribers
        cleanUpTopic(topic);
        return unreadMessages;
    }

    private void cleanUpTopic(String topic) {
        Set<String> subscribers = topicSubscribers.getOrDefault(topic, new HashSet<>());
        for (String message : new ArrayList<>(topicMessages.getOrDefault(topic, new ArrayList<>()))) {
            boolean readByAll = true;
            for (String subscriber : subscribers) {
                Set<String> messagesRead = subscriberMessagesRead.get(subscriber);
                if (!messagesRead.contains(message)) {
                    readByAll = false;
                    break;
                }
            }
            if (readByAll) {
                topicMessages.get(topic).remove(message);
            }
        }
    }
}
