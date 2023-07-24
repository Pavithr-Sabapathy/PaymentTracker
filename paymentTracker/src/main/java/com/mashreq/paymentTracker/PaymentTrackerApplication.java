package com.mashreq.paymentTracker;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mashreq.paymentTracker.configuration.PaymentTrackerConfiguration;
import com.mashreq.paymentTracker.serviceImpl.ReportControllerServiceImpl;

@EntityScan(basePackages = "com.mashreq.paymentTracker.model")
@SpringBootApplication(scanBasePackages = "com.mashreq.paymentTracker")
public class PaymentTrackerApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private PaymentTrackerConfiguration paymentTrackerConfig;;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentTrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String[] controllerBeans = appContext.getBeanNamesForType(ReportControllerServiceImpl.class);
		System.out.println("Beans-->" + controllerBeans.toString());
	}

	@Bean(name = "threadPoolExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(paymentTrackerConfig.getSize());
		executor.setMaxPoolSize(paymentTrackerConfig.getMaxSize());
		executor.setQueueCapacity(paymentTrackerConfig.getQueueCapacity());
		executor.setThreadNamePrefix(paymentTrackerConfig.getThreadPrefix());
		executor.initialize();
		return executor;
	}

}