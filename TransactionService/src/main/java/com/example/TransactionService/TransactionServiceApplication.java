package com.example.TransactionService;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransactionServiceApplication {

	@Bean(destroyMethod = "close")
	public RestHighLevelClient client()
	{
		return new RestHighLevelClient(
				RestClient.builder(
						new HttpHost("localhost", 9200, "http")
				)
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(TransactionServiceApplication.class, args);
	}

}
