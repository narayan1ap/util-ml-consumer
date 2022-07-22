package com.pct.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.pct.consumer.service.impl.Consumer.ImageConsumer;

@EntityScan(basePackages = { "com.pct" })
@ComponentScan(basePackages = { "com.pct" })
@SpringBootApplication
public class UtilsApplication extends SpringBootServletInitializer implements ApplicationRunner {

	@Autowired
	ImageConsumer imageConsumer;

	public static void main(String[] args) {
		SpringApplication.run(UtilsApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(UtilsApplication.class);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		imageConsumer.getCargoCameraImageJson("", null);

	}

}
