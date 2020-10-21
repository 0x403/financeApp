package com.example.APIGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ApiGatewayApplication {

//	@Bean
//	public RestTemplate getRestTemplate() {
//		return new RestTemplate();
//	};

	@Bean
	public WebClient getWebClient() {
		return WebClient.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
