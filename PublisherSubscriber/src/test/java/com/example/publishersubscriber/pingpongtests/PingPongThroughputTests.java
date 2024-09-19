package com.example.publishersubscriber.pingpongtests;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPI;
import com.example.publishersubscriber.clientAPIlibrary.ClientAPIImpl;
import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;
import java.util.List;

public class PingPongThroughputTests {

    public static void main(String[] args) throws InterruptedException {
        MessageBroker messageBroker = new MessageBroker();
        ClientAPI clientAPI = new ClientAPIImpl(messageBroker);

        int[] pairCounts = {1, 2, 3, 4, 5}; // Test for 1 to 5 pairs
        int messageCount = 100; // Number of messages per test

        List<Integer> pairList = new ArrayList<>();  // To store the number of pairs
        List<Double> throughputList = new ArrayList<>();  // To store throughput values

        for (int pairCount : pairCounts) {
            List<String> publisherIds = new ArrayList<>();
            List<String> subscriberIds = new ArrayList<>();
            List<String> topics = new ArrayList<>();

            // Create multiple publisher-subscriber pairs and topics
            for (int i = 0; i < pairCount; i++) {
                String publisherId = clientAPI.registerNewPublisher();
                String subscriberId = clientAPI.registerNewSubscriber();
                String topic = "Topic" + i;

                publisherIds.add(publisherId);
                subscriberIds.add(subscriberId);
                topics.add(topic);

                // Create topic for each publisher and subscribeTopic each subscriber to the respective topic
                clientAPI.createNewTopicToPublisher(publisherId, topic);
                clientAPI.subscribeToTopic(subscriberId, topic);
            }

            System.out.println("Starting ping-pong test with " + pairCount + " pairs...");

            // Benchmarking throughput with multiple pairs
            long startTime = System.nanoTime();

            // Multiple publishers sending messages in parallel
            for (int i = 0; i < pairCount; i++) {
                String publisherId = publisherIds.get(i);
                String topic = topics.get(i);
                for (int j = 0; j < messageCount; j++) {
                    clientAPI.sendMessageToTopic(publisherId, topic, "Benchmark Message " + j);
                }
            }

            // Multiple subscribers pulling messages in parallel
            for (int i = 0; i < pairCount; i++) {
                String subscriberId = subscriberIds.get(i);
                String topic = topics.get(i);
                for (int j = 0; j < messageCount; j++) {
                    clientAPI.pullMessagesFromPool(subscriberId, topic);
                }
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            double throughput = (double) (pairCount * messageCount) / (duration / 1000.0); // Messages per second

            System.out.println("Throughput with " + pairCount + " pairs: " + throughput + " messages/second");

            // Add results to lists for graph plotting
            pairList.add(pairCount);
            throughputList.add(throughput);
        }

        // Plot the graph with XChart
        plotThroughputGraph(pairList, throughputList);
    }

    // Function to plot the throughput graph using XChart
    private static void plotThroughputGraph(List<Integer> pairList, List<Double> throughputList) {
        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Server Throughput with Multiple Pairs")
                .xAxisTitle("Number of Publisher-Subscriber Pairs").yAxisTitle("Throughput (Messages/Second)").build();

        // Add the data series to the chart
        chart.addSeries("Throughput", pairList, throughputList);

        // Display the chart
        new SwingWrapper<>(chart).displayChart();
    }
}
