package com.example.publishersubscriber.serverprogram;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageBroker {
    // Data structures to hold topics and messages
    // Map of String and List of String to store topics and messages corresponding to that topic
    private final Map<String, List<String>> topicMessages = new HashMap<>();
    // Map of String and Set of String to store topics subscribed by each subscriber
    private final Map<String, Set<String>> topicSubscribers = new HashMap<>();
    // Map of String and Set of String to log the messages already read by the subscriber
    private final Map<String, Set<String>> subscriberMessagesRead = new HashMap<>();

    // PublisherId counter
    private int publisherIdCounter = 1;
    // SubscriberId counter
    private int subscriberIdCounter = 1;

    // Register Publisher with a format "PUB-#number"
    public String registerNewPublisher() {
        return "PUB-" + publisherIdCounter++;
    }

    // Render Create Topic into a component bean/server
    public void createNewTopicToPublisher(String publisherId, String topic) {
        // If the topic is not already registered under the publisher, register the topic
        if (!topicMessages.containsKey(topic)) {
            // Bridge between Publisher and Subscriber
            topicMessages.put(topic, new ArrayList<>());
            topicSubscribers.put(topic, new HashSet<>());
        }
    }

    // Render Delete Topic into a component bean/server
    public void deleteTopicFromPublisher(String publisherId, String topic) {
        // Bridge between Publisher and Subscriber
        topicMessages.remove(topic);
        topicSubscribers.remove(topic);
    }

    // Render Send Message to Topic into a component bean/server
    public void sendMessageToTopic(String publisherId, String topic, String message) {
        // Fetch the unread messages from the pool
        List<String> messages = topicMessages.getOrDefault(topic, new ArrayList<>());
        messages.add(message);
        topicMessages.put(topic, messages);
    }

    // Render Register Subscriber into a component bean/server
    public String registerSubscriber() {
        // Register the subscriber with a format "SUB-#number"
        String subscriberId = "SUB-" + subscriberIdCounter++;
        subscriberMessagesRead.put(subscriberId, new HashSet<>());
        return subscriberId;
    }

    // Render Subscribe to a Topic into a component bean/server
    public void subscribeToTopic(String subscriberId, String topic) {
        // Subscribe to the given topic
        Set<String> subscribers = topicSubscribers.getOrDefault(topic, new HashSet<>());
        subscribers.add(subscriberId);
        topicSubscribers.put(topic, subscribers);
    }

    // Render Pull Messages from Topic into a component bean/server
    public List<String> pullMessagesFromPool(String subscriberId, String topic) {
        List<String> messages = new ArrayList<>(topicMessages.getOrDefault(topic, new ArrayList<>()));
        Set<String> messagesRead = subscriberMessagesRead.getOrDefault(subscriberId, new HashSet<>());
        // Manage the read and unread messages
        List<String> unreadMessages = new ArrayList<>();
        for (String message : messages) {
            if (!messagesRead.contains(message)) {
                unreadMessages.add(message);
                messagesRead.add(message);
            }
        }
        subscriberMessagesRead.put(subscriberId, messagesRead);
        // We only clean all the messages from te buffer after they are read by ALL subscribers
        // Remove messages that have been read by all subscribers
        cleanUpTopic(topic);
        return unreadMessages;
    }

    // Buffer clean up function to clean up memory
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
