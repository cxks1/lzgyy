package com.lzgyy.config.dubbo.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

/**
 * dubbo生产者初始化
 */
@Configuration
@EnableConfigurationProperties(DubboProviderPropertiesConfig.class)
public class DubboProviderConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DubboProviderConfiguration.class);
	
	@Autowired
	private DubboProviderPropertiesConfig dubboPropertiesConfig;
	
	@Bean
    public ApplicationConfig applicationConfig() {
		logger.info("***** DubboProviderConfiguration ApplicationConfig ...");
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(dubboPropertiesConfig.getApplicationName());
        applicationConfig.setOwner(dubboPropertiesConfig.getApplicationOwner());
        logger.info("***** DubboProviderConfiguration ApplicationConfig finished");
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
    	logger.info("***** DubboProviderConfiguration RegistryConfig ...");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboPropertiesConfig.getRegistryAddress());
        if(!StringUtils.isBlank(dubboPropertiesConfig.getRegistryClient())) {
        	registryConfig.setClient(dubboPropertiesConfig.getRegistryClient());
        }
        
        if(!StringUtils.isBlank(dubboPropertiesConfig.getRegistryProtocol())) {
        	 registryConfig.setProtocol(dubboPropertiesConfig.getRegistryProtocol());
        }
       
        if(null != dubboPropertiesConfig.getPort()) {
        	 registryConfig.setPort(dubboPropertiesConfig.getPort());
        }
        logger.info("***** DubboProviderConfiguration RegistryConfig finished");
        return registryConfig;
    }
}