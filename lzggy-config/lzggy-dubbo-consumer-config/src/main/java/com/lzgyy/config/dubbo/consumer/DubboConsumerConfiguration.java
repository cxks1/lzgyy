package com.lzgyy.config.dubbo.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.RegistryConfig;

/**
 * dubbo消费者初始化
 */
@Configuration
@EnableConfigurationProperties(DubboConsumerPropertiesConfig.class)
public class DubboConsumerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DubboConsumerConfiguration.class);

	@Autowired
	private DubboConsumerPropertiesConfig dubboPropertiesConfig;

	@Bean
	public ApplicationConfig applicationConfig() {
		logger.info("***** DubboConsumerConfiguration ApplicationConfig ...");
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(dubboPropertiesConfig.getApplicationName());
		applicationConfig.setOwner(dubboPropertiesConfig.getApplicationOwner());
		logger.info("***** DubboConsumerConfiguration ApplicationConfig finished ");
		return applicationConfig;
	}

	@Bean
	public ConsumerConfig consumerConfig() {
		logger.info("***** DubboConsumerConfiguration ConsumerConfig ...");
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setTimeout(dubboPropertiesConfig.getTimeout());
		consumerConfig.setCheck(dubboPropertiesConfig.isCheck());
		logger.info("***** DubboConsumerConfiguration ConsumerConfig finished");
		return consumerConfig;
	}

	@Bean
	public RegistryConfig registryConfig() {
		logger.info("***** DubboConsumerConfiguration RegistryConfig ...");
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(dubboPropertiesConfig.getRegistryAddress());
		registryConfig.setClient(dubboPropertiesConfig.getRegistryClient());
		registryConfig.setProtocol(dubboPropertiesConfig.getRegistryProtocol());
		logger.info("***** DubboConsumerConfiguration RegistryConfig finished");
		return registryConfig;
	}
}