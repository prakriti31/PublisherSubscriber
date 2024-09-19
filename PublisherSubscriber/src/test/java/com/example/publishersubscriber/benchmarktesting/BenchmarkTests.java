package com.example.publishersubscriber.benchmarktesting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            executor.submit(() -> {
                restTemplate.postForEntity(baseUrl + REGISTER_PUBLISHER_ENDPOINT, null, String.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for registerPublisher: " + duration + " ms");
        System.out.println("Throughput (publishers/sec): " + throughput);

        writeToCSV("register_publisher.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkCreateTopic(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                restTemplate.postForEntity(baseUrl + CREATE_TOPIC_ENDPOINT, request, Void.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for createTopic: " + duration + " ms");
        System.out.println("Throughput (topics/sec): " + throughput);

        writeToCSV("create_topic.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkDeleteTopic(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher();
        createTopic(publisherId);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                restTemplate.delete(baseUrl + DELETE_TOPIC_ENDPOINT, request);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for deleteTopic: " + duration + " ms");
        System.out.println("Throughput (topics deleted/sec): " + throughput);

        writeToCSV("delete_topic.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkSendMessage(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher();
        createTopic(publisherId);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                request.put("message", TEST_MESSAGE);
                restTemplate.postForEntity(baseUrl + SEND_MESSAGE_ENDPOINT, request, Void.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for sendMessage: " + duration + " ms");
        System.out.println("Throughput (messages/sec): " + throughput);

        writeToCSV("send_message.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkRegisterSubscriber(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            executor.submit(() -> {
                restTemplate.postForEntity(baseUrl + REGISTER_SUBSCRIBER_ENDPOINT, null, String.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for registerSubscriber: " + duration + " ms");
        System.out.println("Throughput (subscribers/sec): " + throughput);

        writeToCSV("register_subscriber.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkSubscribeTopic(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String subscriberId = registerSubscriber();
        createTopic(registerPublisher());

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("subscriberId", subscriberId);
                request.put("topic", "TestTopic" + clientIndex);
                restTemplate.postForEntity(baseUrl + SUBSCRIBE_TOPIC_ENDPOINT, request, Void.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for subscribeTopic: " + duration + " ms");
        System.out.println("Throughput (subscriptions/sec): " + throughput);

        writeToCSV("subscribe_topic.csv", numClients + "," + throughput);
        return throughput;
    }

    public double benchmarkPullMessages(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String subscriberId = registerSubscriber();
        createTopic(registerPublisher());
        subscribeTopic(subscriberId, "TestTopic");

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            executor.submit(() -> {
                restTemplate.getForEntity(baseUrl + PULL_MESSAGES_ENDPOINT + "?subscriberId=" + subscriberId + "&topic=TestTopic", List.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(THREAD_TERMINATION_TIMEOUT, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        double throughput = (numClients * 1000.0 / duration);
        System.out.println("Clients: " + numClients + " | Time taken for pullMessagesFromTopic: " + duration + " ms");
        System.out.println("Throughput (messages pulled/sec): " + throughput);

        writeToCSV("pull_messages.csv", numClients + "," + throughput);
        return throughput;
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

        final int initialClients = 10;
        final int maxClients = 100;
        final int increment = 10;

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
            writer.write("Number of Clients,Throughput");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
