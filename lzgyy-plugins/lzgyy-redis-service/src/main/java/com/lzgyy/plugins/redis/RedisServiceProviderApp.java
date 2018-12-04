package com.lzgyy.plugins.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.lzgyy.config.dubbo.provider.DubboProviderConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@Import({DubboProviderConfiguration.class})
@DubboComponentScan(basePackages="com.lzgyy.plugins.redis.service.impl")
public class RedisServiceProviderApp {

	private static final Logger logger = LogManager.getLogger(RedisServiceProviderApp.class);
	
    public static void main( String[] args ) {
    	logger.info("***** RedisServiceProviderApp starting ... ");
    	SpringApplication.run(RedisServiceProviderApp.class, args);
    	logger.info("***** RedisServiceProviderApp started ");
    }
}