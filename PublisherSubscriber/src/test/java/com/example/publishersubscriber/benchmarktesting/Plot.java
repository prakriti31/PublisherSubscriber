package com.example.publishersubscriber.benchmarktesting;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Plot {

    private static final String[] FILES = {
            "register_publisher.csv",
            "create_topic.csv",
            "delete_topic.csv",
            "send_message.csv",
            "register_subscriber.csv",
            "subscribe_topic.csv",
            "pull_messages.csv"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Benchmark Results");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

            for (String file : FILES) {
                try {
                    XYChart chart = createChart(file);
                    JPanel chartPanel = new XChartPanel<>(chart);
                    chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));
                    frame.add(chartPanel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            frame.pack();
            frame.setVisible(true);
        });
    }

    private static XYChart createChart(String fileName) throws IOException {
        List<Integer> numClientsList = new ArrayList<>();
        List<Double> throughputList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int numClients = Integer.parseInt(parts[0].trim());
                        double throughput = Double.parseDouble(parts[1].trim());
                        numClientsList.add(numClients);
                        throughputList.add(throughput);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping line due to number format issue: " + line);
                    }
                } else {
                    System.err.println("Skipping line due to incorrect format: " + line);
                }
            }
        }

        if (numClientsList.isEmpty() || throughputList.isEmpty()) {
            throw new IllegalArgumentException("Y-Axis data cannot be empty!!!");
        }

        XYChart chart = new XYChartBuilder().width(800).height(400).title(getChartTitle(fileName))
                .xAxisTitle("Number of Clients").yAxisTitle("Throughput (per second)").build();

        chart.addSeries("Throughput", numClientsList, throughputList);

        return chart;
    }

    private static String getChartTitle(String fileName) {
        return switch (fileName) {
            case "register_publisher.csv" -> "Register Publisher Benchmark";
            case "create_topic.csv" -> "Create Topic Benchmark";
            case "delete_topic.csv" -> "Delete Topic Benchmark";
            case "send_message.csv" -> "Send Message Benchmark";
            case "register_subscriber.csv" -> "Register Subscriber Benchmark";
            case "subscribe_topic.csv" -> "Subscribe Topic Benchmark";
            case "pull_messages.csv" -> "Pull Messages Benchmark";
            default -> "Benchmark Results";
        };
    }
}
