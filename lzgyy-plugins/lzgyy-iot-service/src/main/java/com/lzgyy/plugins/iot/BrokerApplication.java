package com.lzgyy.plugins.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.lzgyy.plugins.iot.service.mqtt.broker.config.BrokerProperties;

@SpringBootApplication(scanBasePackages = {"com.lzgyy.plugins.iot"})
public class BrokerApplication {
	
	@Bean
	public BrokerProperties brokerProperties() {
		return new BrokerProperties();
	}
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BrokerApplication.class);
		System.setProperty("user.timezone","Asia/Shanghai"); //设置时区
		//System.setProperty("https.protocols", "TLSv1");
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

}