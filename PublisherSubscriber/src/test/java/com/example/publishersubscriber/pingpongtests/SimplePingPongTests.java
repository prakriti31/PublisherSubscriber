package com.example.publishersubscriber.pingpongtests;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;

import java.util.List;

public class SimplePingPongTests {

    public static void main(String[] args) {
        MessageBroker messageBroker = new MessageBroker();
        ClientAPI clientAPI = new ClientAPIImpl(messageBroker);

        // Register two publishers
        String publisher1 = clientAPI.registerNewPublisher();
        String publisher2 = clientAPI.registerNewPublisher();

        // Create two topics
        String topic1 = "Topic1";
        String topic2 = "Topic2";

        clientAPI.createNewTopicToPublisher(publisher1, topic1);
        clientAPI.createNewTopicToPublisher(publisher2, topic2);

        // Register two subscribers
        String subscriber1 = clientAPI.registerNewSubscriber();
        String subscriber2 = clientAPI.registerNewSubscriber();

        // Subscribe subscribers to the topics
        clientAPI.subscribeToTopic(subscriber1, topic1);
        clientAPI.subscribeToTopic(subscriber2, topic2);

        // Send message from publisher1 to topic1
        String message1 = "Message from Publisher 1 to Topic 1";
        clientAPI.sendMessageToTopic(publisher1, topic1, message1);

        // Subscriber 1 pulls message from topic1
        List<String> messagesSubscriber1 = clientAPI.pullMessagesFromPool(subscriber1, topic1);
        System.out.println("Subscriber 1 received: " + messagesSubscriber1);

        // Send message from publisher2 to topic2
        String message2 = "Message from Publisher 2 to Topic 2";
        clientAPI.sendMessageToTopic(publisher2, topic2, message2);

        // Subscriber 2 pulls message from topic2
        List<String> messagesSubscriber2 = clientAPI.pullMessagesFromPool(subscriber2, topic2);
        System.out.println("Subscriber 2 received: " + messagesSubscriber2);

        // Sending messages in Ping-Pong fashion
        for (int i = 0; i < 5; i++) {
            String ping = "Ping " + i + " from Publisher 1";
            clientAPI.sendMessageToTopic(publisher1, topic1, ping);
            messagesSubscriber1 = clientAPI.pullMessagesFromPool(subscriber1, topic1);
            System.out.println("Subscriber 1 received: " + messagesSubscriber1);

            String pong = "Pong " + i + " from Publisher 2";
            clientAPI.sendMessageToTopic(publisher2, topic2, pong);
            messagesSubscriber2 = clientAPI.pullMessagesFromPool(subscriber2, topic2);
            System.out.println("Subscriber 2 received: " + messagesSubscriber2);
        }

        // Benchmarking maximum throughput
        long startTime = System.nanoTime();
        int messageCount = 1000;

        for (int i = 0; i < messageCount; i++) {
            clientAPI.sendMessageToTopic(publisher1, topic1, "Message " + i);
            clientAPI.pullMessagesFromPool(subscriber1, topic1);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        double throughput = (double) messageCount / (duration / 1000.0); // Messages per second

        System.out.println("Max Throughput: " + throughput + " messages/second");
    }
}
