package com.example.publishersubscriber.benchmarktesting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BenchmarkTests {
    //./gradlew bootRun

    private final RestTemplate restTemplate;
    private final String baseUrl;

    // Constants
    private static final int THREAD_TERMINATION_TIMEOUT = 1; // in minutes
    private static final String REGISTER_PUBLISHER_ENDPOINT = "/api/publisher/register";
    private static final String CREATE_TOPIC_ENDPOINT = "/api/publisher/createNewTopicToPublisher";
    private static final String DELETE_TOPIC_ENDPOINT = "/api/publisher/deleteTopicFromPublisher";
    private static final String SEND_MESSAGE_ENDPOINT = "/api/publisher/sendMessageToTopic";
    private static final String REGISTER_SUBSCRIBER_ENDPOINT = "/api/subscriber/register";
    private static final String SUBSCRIBE_TOPIC_ENDPOINT = "/api/subscriber/subscribeToTopic";
    private static final String PULL_MESSAGES_ENDPOINT = "/api/subscriber/pullMessagesFromPool";
    private static final String TEST_MESSAGE = "TestMessage";

    // Threshold for server refusal
    private static final int CLIENT_TIMEOUT_THRESHOLD = 6000;

    public BenchmarkTests(String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    // Method to write results to CSV
    private void writeToCSV(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methods to benchmark each API
    public double benchmarkRegisterPublisher(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, REGISTER_PUBLISHER_ENDPOINT, "register_publisher.csv");
    }

    public double benchmarkCreateTopic(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, CREATE_TOPIC_ENDPOINT, "create_topic.csv");
    }

    public double benchmarkDeleteTopic(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, DELETE_TOPIC_ENDPOINT, "delete_topic.csv");
    }

    public double benchmarkSendMessage(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, SEND_MESSAGE_ENDPOINT, "send_message.csv");
    }

    public double benchmarkRegisterSubscriber(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, REGISTER_SUBSCRIBER_ENDPOINT, "register_subscriber.csv");
    }

    public double benchmarkSubscribeTopic(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, SUBSCRIBE_TOPIC_ENDPOINT, "subscribe_topic.csv");
    }

    public double benchmarkPullMessages(int numClients) throws InterruptedException {
        return benchmarkAPI(numClients, PULL_MESSAGES_ENDPOINT, "pull_messages.csv");
    }

    private double benchmarkAPI(int numClients, String endpoint, String csvFileName) throws InterruptedException {
        // Check for timeout condition after 6000 clients
        if (numClients > CLIENT_TIMEOUT_THRESHOLD) {
            System.out.println("Server refused to connect after " + CLIENT_TIMEOUT_THRESHOLD + " clients.");
            return -1; // Return an error code or custom value indicating timeout.
        }

        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            executor.submit(() -> {
                restTemplate.postForEntity(baseUrl + endpoint, null, String.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double timeTakenInSeconds = duration / 1000.0;
        System.out.println("Clients: " + numClients + " | Time taken for " + endpoint + ": " + timeTakenInSeconds + " seconds");

        writeToCSV(csvFileName, numClients + "," + timeTakenInSeconds);
        return timeTakenInSeconds;
    }

    private String registerPublisher() {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + REGISTER_PUBLISHER_ENDPOINT, null, String.class);
        return response.getBody();
    }

    private void createTopic(String publisherId) {
        Map<String, String> request = new HashMap<>();
        request.put("publisherId", publisherId);
        request.put("topic", "TestTopic");
        restTemplate.postForEntity(baseUrl + CREATE_TOPIC_ENDPOINT, request, Void.class);
    }

    private String registerSubscriber() {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + REGISTER_SUBSCRIBER_ENDPOINT, null, String.class);
        return response.getBody();
    }

    private void subscribeTopic(String subscriberId, String topic) {
        Map<String, String> request = new HashMap<>();
        request.put("subscriberId", subscriberId);
        request.put("topic", topic);
        restTemplate.postForEntity(baseUrl + SUBSCRIBE_TOPIC_ENDPOINT, request, Void.class);
    }

    public static void main(String[] args) throws InterruptedException {
        BenchmarkTests benchmarkTestsClient = new BenchmarkTests("http://localhost:8080");

        final int initialClients = 100;
        final int maxClients = 10000;
        final int increment = 500;

        // Create CSV files with headers
        createCSVFile("register_publisher.csv");
        createCSVFile("create_topic.csv");
        createCSVFile("delete_topic.csv");
        createCSVFile("send_message.csv");
        createCSVFile("register_subscriber.csv");
        createCSVFile("subscribe_topic.csv");
        createCSVFile("pull_messages.csv");

        for (int numClients = initialClients; numClients <= maxClients; numClients += increment) {
            System.out.println("\nRunning benchmark with " + numClients + " clients:");

            benchmarkTestsClient.benchmarkRegisterPublisher(numClients);
            benchmarkTestsClient.benchmarkCreateTopic(numClients);
            benchmarkTestsClient.benchmarkDeleteTopic(numClients);
            benchmarkTestsClient.benchmarkSendMessage(numClients);
            benchmarkTestsClient.benchmarkRegisterSubscriber(numClients);
            benchmarkTestsClient.benchmarkSubscribeTopic(numClients);
            benchmarkTestsClient.benchmarkPullMessages(numClients);
        }
    }

    private static void createCSVFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Number of Clients,Time Taken (seconds)");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
