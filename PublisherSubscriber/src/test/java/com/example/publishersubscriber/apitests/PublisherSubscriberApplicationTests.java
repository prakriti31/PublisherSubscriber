package com.example.publishersubscriber.apitests;

import com.example.publishersubscriber.clientAPIlibrary.ClientAPIController;
import com.example.publishersubscriber.serverprogram.MessageBroker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.*;

@WebMvcTest(ClientAPIController.class)
public class PublisherSubscriberApplicationTests {

    private MockMvc mockMvc;

    @Mock
    private MessageBroker messageBroker;

    @InjectMocks
    private ClientAPIController clientAPIController;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    void testRegisterPublisher() throws Exception {
        String publisherId = "PUB-1";
//        when(clientAPIController.registerPublisher()).thenReturn(ResponseEntity.ok(publisherId));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(publisherId));
    }

    @Test
    @Order(2)
    void testCreateTopic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/createNewTopicToPublisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publisherId\":\"PUB-1\",\"topic\":\"PubTopic1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testSendMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/sendMessageToTopic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publisherId\":\"PUB-1\",\"topic\":\"PubTopic1\",\"message\":\"Published Message 1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void testRegisterSubscriber() throws Exception {
        String subscriberId = "SUB-1";
//        when(clientAPIController.registerSubscriber()).thenReturn(ResponseEntity.ok(subscriberId));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscriber/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(subscriberId));
    }

    @Test
    @Order(5)
    void testSubscribeToTopic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscriber/subscribeToTopic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subscriberId\":\"SUB-1\",\"topic\":\"PubTopic1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void testPullMessagesFromPool() throws Exception {
        String jsonResponse = "[\"Published Message 1\"]";
//        when(clientAPIController.pullMessages("SUB-1", "PubTopic1")).thenReturn(ResponseEntity.ok(List.of("Message1", "Message2")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/subscriber/pullMessagesFromPool")
                        .param("subscriberId", "SUB-1")
                        .param("topic", "PubTopic1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @Order(7)  // Executes this test at the end
    void testDeleteTopic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/publisher/deleteTopicFromPublisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publisherId\":\"PUB-1\",\"topic\":\"PubTopic1\"}"))
                .andExpect(status().isOk());
    }
}

