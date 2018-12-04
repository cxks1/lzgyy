package com.lzgyy.plugins.iot.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = {"com.lzgyy.plugins.iot"})
public class ClientApplication {
	
    public static void main( String[] args ){
        run(ClientApplication.class, args);
    }
}