package com.javatechie.spring.batch.api;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableBatchProcessing
public class SpringBatchMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchMongoApplication.class, args);
	}
}
