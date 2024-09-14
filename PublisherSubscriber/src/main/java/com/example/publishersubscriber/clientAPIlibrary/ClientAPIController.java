package com.example.publishersubscriber.clientAPIlibrary;

import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClientAPIController {

    // Interface interacting with messageBroker
    private final ClientAPI clientAPI;

    // Constructor of Controller instantiating messageBroker to instantiate ClientAPIImplementation
    public ClientAPIController(MessageBroker messageBroker) {
        this.clientAPI = new ClientAPIImpl(messageBroker);
    }

    @PostMapping("/publisher/register")
    public ResponseEntity<String> registerPublisher() {
        String publisherId = clientAPI.registerNewPublisher();
        return ResponseEntity.ok(publisherId);
    }

    @PostMapping("/publisher/createNewTopicToPublisher")
    public ResponseEntity<Void> createTopic(@RequestBody Map<String, String> request) {
        clientAPI.createNewTopicToPublisher(request.get("publisherId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/publisher/deleteTopicFromPublisher")
    public ResponseEntity<Void> deleteTopic(@RequestBody Map<String, String> request) {
        clientAPI.deleteTopicFromPublisher(request.get("publisherId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/publisher/sendMessageToTopic")
    public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> request) {
        clientAPI.sendMessageToTopic(request.get("publisherId"), request.get("topic"), request.get("message"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriber/register")
    public ResponseEntity<String> registerSubscriber() {
        String subscriberId = clientAPI.registerNewSubscriber();
        return ResponseEntity.ok(subscriberId);
    }

    @PostMapping("/subscriber/subscribeToTopic")
    public ResponseEntity<Void> subscribe(@RequestBody Map<String, String> request) {
        clientAPI.subscribeToTopic(request.get("subscriberId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscriber/pullMessagesFromPool")
    public ResponseEntity<List<String>> pullMessages(@RequestParam String subscriberId, @RequestParam String topic) {
        List<String> messages = clientAPI.pullMessagesFromPool(subscriberId, topic);
        return ResponseEntity.ok(messages);
    }
}
