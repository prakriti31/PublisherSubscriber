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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ClientAPIController.class)
public class PublisherSubscriberApplicationTests {

    // Declare a mocksvc
    private MockMvc mockMvc;

    // Declare a message broker private to this class
    @Mock
    private MessageBroker messageBroker;

    // Declare a client API controller private to this class
    @InjectMocks
    private ClientAPIController clientAPIController;

    // Set Up
    @BeforeEach
    void setUp(WebApplicationContext wac) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // Test registerPublisher API
    @Test
    @Order(1)
    void testRegisterPublisher() throws Exception {
        String publisherId = "PUB-1";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(publisherId));
    }

    // Test createTopic API
    @Test
    @Order(2)
    void testCreateTopic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/createNewTopicToPublisher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publisherId\":\"PUB-1\",\"topic\":\"PubTopic1\"}"))
                .andExpect(status().isOk());
    }

    // Test sendMessage API
    @Test
    @Order(3)
    void testSendMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/publisher/sendMessageToTopic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publisherId\":\"PUB-1\",\"topic\":\"PubTopic1\",\"message\":\"Published Message 1\"}"))
                .andExpect(status().isOk());
    }

    // Test registerSubscriber API
    @Test
    @Order(4)
    void testRegisterSubscriber() throws Exception {
        String subscriberId = "SUB-1";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscriber/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(subscriberId));
    }

    // Test subscribeToTopic API
    @Test
    @Order(5)
    void testSubscribeToTopic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscriber/subscribeToTopic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subscriberId\":\"SUB-1\",\"topic\":\"PubTopic1\"}"))
                .andExpect(status().isOk());
    }

    // Test pullMessagesFromPool API
    @Test
    @Order(6)
    void testPullMessagesFromPool() throws Exception {
        String jsonResponse = "[\"Published Message 1\"]";

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

