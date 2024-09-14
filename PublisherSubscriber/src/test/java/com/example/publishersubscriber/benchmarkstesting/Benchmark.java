package com.example.publishersubscriber.benchmarkstesting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public Benchmark (String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    // Method to benchmark CreateTopic API
    public void benchmarkCreateTopic(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher(); // Register a publisher to use its ID

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                restTemplate.postForEntity(baseUrl + "/api/publisher/createNewTopicToPublisher", request, Void.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // Wait for all threads to finish
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Clients: " + numClients + " | Time taken for createTopic: " + duration + " ms");
        System.out.println("Throughput (topics/sec): " + (numClients * 1000.0 / duration));
    }

    // Repeat the same method for other APIs:

    // Benchmark registerPublisher API
    public void benchmarkRegisterPublisher(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            executor.submit(() -> {
                restTemplate.postForEntity(baseUrl + "/api/publisher/register", null, String.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Clients: " + numClients + " | Time taken for registerPublisher: " + duration + " ms");
        System.out.println("Throughput (publishers/sec): " + (numClients * 1000.0 / duration));
    }

    // Benchmark deleteTopic API
    public void benchmarkDeleteTopic(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher(); // Register a publisher to use its ID
        createTopic(publisherId); // Ensure the topic exists

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                restTemplate.delete(baseUrl + "/api/publisher/deleteTopicFromPublisher", request);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Clients: " + numClients + " | Time taken for deleteTopic: " + duration + " ms");
        System.out.println("Throughput (topics deleted/sec): " + (numClients * 1000.0 / duration));
    }

    // Benchmark sendMessage API
    public void benchmarkSendMessage(int numClients) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String publisherId = registerPublisher(); // Register a publisher to use its ID
        createTopic(publisherId); // Ensure the topic exists

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numClients; i++) {
            final int clientIndex = i + 1;
            executor.submit(() -> {
                Map<String, String> request = new HashMap<>();
                request.put("publisherId", publisherId);
                request.put("topic", "TestTopic" + clientIndex);
                request.put("message", "TestMessage");
                restTemplate.postForEntity(baseUrl + "/api/publisher/sendMessageToTopic", request, Void.class);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Clients: " + numClients + " | Time taken for sendMessage: " + duration + " ms");
        System.out.println("Throughput (messages/sec): " + (numClients * 1000.0 / duration));
    }

    // Helper method to register a publisher and return its ID
    private String registerPublisher() {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/publisher/register", null, String.class);
        return response.getBody();
    }

    // Helper method to create a topic
    private void createTopic(String publisherId) {
        Map<String, String> request = new HashMap<>();
        request.put("publisherId", publisherId);
        request.put("topic", "TestTopic");
        restTemplate.postForEntity(baseUrl + "/api/publisher/createNewTopicToPublisher", request, Void.class);
    }

    // Main method to run all benchmarks
    public static void main(String[] args) throws InterruptedException {
        Benchmark benchmarkClient = new Benchmark("http://localhost:8080");

        int initialClients = 10; // Start with 10 clients
        int maxClients = 100;    // Maximum clients to test with
        int increment = 10;      // Increment by 10 clients each time

        for (int numClients = initialClients; numClients <= maxClients; numClients += increment) {
            System.out.println("\nRunning benchmark with " + numClients + " clients:");

            benchmarkClient.benchmarkCreateTopic(numClients);
            benchmarkClient.benchmarkRegisterPublisher(numClients);
            benchmarkClient.benchmarkDeleteTopic(numClients);
            benchmarkClient.benchmarkSendMessage(numClients);
        }
    }
}

/*
*
* Fine-Tune the Number of Clients: You can adjust initialClients, maxClients, and increment to explore
* the server's performance more thoroughly.
Monitor Server Load: If possible, monitor the server’s CPU, memory, and network usage during the tests
* to identify potential bottlenecks.
*
* */