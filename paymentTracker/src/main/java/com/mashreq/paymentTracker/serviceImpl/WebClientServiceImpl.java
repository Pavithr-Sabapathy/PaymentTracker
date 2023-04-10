package com.mashreq.paymentTracker.serviceImpl;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.service.WebClientService;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.validation.Valid;
import reactor.netty.http.client.HttpClient;

@Component
public class WebClientServiceImpl implements WebClientService {

	@Override
	public DataSourceConfig getDataSourceConfigById(Long dataSourceId) {
		HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofMillis(5000))
				.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
		client = WebClient.create();
		ResponseSpec responseSpec = client.get().uri("http://localhost:8080/paymentTracker/api/dataSource/1")
				/*
				 * .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github.v3+json")
				 * .header("Authorization", "Basic " + Base64Utils .encodeToString((username +
				 * ":" + token).getBytes(UTF_8)))
				 */
				.retrieve();
		DataSourceConfig responseBody = responseSpec.bodyToMono(DataSourceConfig.class).block();
		return responseBody;
	}

	@Override
	public String saveDataSourceConfig(@Valid DataSourceConfig dataSourceConfigurationRequest) {
		// TODO Auto-generated method stub
		HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofMillis(5000))
				.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
		client = WebClient.create();
		String responseSpec = client.post().uri("http://localhost:8080/paymentTracker/api/dataSource/save")
				.bodyValue(dataSourceConfigurationRequest).retrieve().bodyToMono(String.class).block();
		return responseSpec;
	}

}