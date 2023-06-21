package com.mashreq.paymentTracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI OpenAPI() {
		return new OpenAPI().info(new Info().title("Payment Tracker").description("API Documentation").version("1.0"));
	}

}
