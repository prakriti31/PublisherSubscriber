package com.example.publishersubscriber.connectionestablishmenttests;

import com.example.publishersubscriber.clientprogram.PublisherClient;
import com.example.publishersubscriber.clientprogram.SubscriberClient;
import com.example.publishersubscriber.serverprogram.MessageBroker;

public class TestMultipleConnection {

    public static void main(String[] args) {
        // Create a shared MessageBroker instance
        MessageBroker sharedMessageBroker = new MessageBroker();

        // Define the number of publishers and subscribers
        int numPublishers = 3;
        int numSubscribers = 5;

        // Array to hold publisher and subscriber threads
        Thread[] publishers = new Thread[numPublishers];
        Thread[] subscribers = new Thread[numSubscribers];

        // Start multiple publisher clients
        for (int i = 0; i < numPublishers; i++) {
            final int publisherIndex = i + 1;
            PublisherClient publisherClient = new PublisherClient(sharedMessageBroker);
            publishers[i] = new Thread(() -> publisherClient.executePublisher("Publisher " + publisherIndex + " Message 1", "Publisher " + publisherIndex + " Message 2"));
            publishers[i].start();
        }

        // Start multiple subscriber clients
        for (int i = 0; i < numSubscribers; i++) {
            final int subscriberIndex = i + 1;
            SubscriberClient subscriberClient = new SubscriberClient(sharedMessageBroker);
            subscribers[i] = new Thread(() -> subscriberClient.runSubscriber("education"));
            subscribers[i].start();
        }

        // Wait for all publisher threads to finish
        for (Thread publisher : publishers) {
            try {
                publisher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for all subscriber threads to finish
        for (Thread subscriber : subscribers) {
            try {
                subscriber.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All clients have completed.");
    }
}
