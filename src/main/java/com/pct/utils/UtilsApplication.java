package com.pct.utils;

import java.util.ArrayList;
import java.util.List;

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
//		List<String> uuids = new ArrayList<>();
//		uuids.add("c1a42203-6ecc-48bd-a6cc-a94f156acb43");
//		uuids.add("111da730-d961-4efc-879e-8a0e8b4bcc7f");
//		imageConsumer.getCargoCameraImageJson(uuids, null);

	}

}
