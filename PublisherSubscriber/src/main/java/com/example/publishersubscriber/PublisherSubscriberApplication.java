package com.example.publishersubscriber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.publishersubscriber")
public class PublisherSubscriberApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublisherSubscriberApplication.class, args);
	}

}
