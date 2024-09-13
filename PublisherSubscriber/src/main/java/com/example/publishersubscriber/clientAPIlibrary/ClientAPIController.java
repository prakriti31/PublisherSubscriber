package com.example.publishersubscriber.clientAPIlibrary;

import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClientAPIController {

    private final ClientAPI clientAPI;

    public ClientAPIController(MessageBroker messageBroker) {
        this.clientAPI = new ClientAPIImpl(messageBroker);
    }

    @PostMapping("/publisher/register")
    public ResponseEntity<String> registerPublisher() {
        String publisherId = clientAPI.registerPublisher();
        return ResponseEntity.ok(publisherId);
    }

    @PostMapping("/publisher/createTopic")
    public ResponseEntity<Void> createTopic(@RequestBody Map<String, String> request) {
        clientAPI.createTopic(request.get("publisherId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/publisher/deleteTopic")
    public ResponseEntity<Void> deleteTopic(@RequestBody Map<String, String> request) {
        clientAPI.deleteTopic(request.get("publisherId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/publisher/sendMessage")
    public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> request) {
        clientAPI.sendMessage(request.get("publisherId"), request.get("topic"), request.get("message"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriber/register")
    public ResponseEntity<String> registerSubscriber() {
        String subscriberId = clientAPI.registerSubscriber();
        return ResponseEntity.ok(subscriberId);
    }

    @PostMapping("/subscriber/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody Map<String, String> request) {
        clientAPI.subscribe(request.get("subscriberId"), request.get("topic"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscriber/pullMessages")
    public ResponseEntity<List<String>> pullMessages(@RequestParam String subscriberId, @RequestParam String topic) {
        List<String> messages = clientAPI.pullMessages(subscriberId, topic);
        return ResponseEntity.ok(messages);
    }
}
