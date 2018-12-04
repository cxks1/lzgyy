package com.lzgyy.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@PropertySource(value = "classpath:application-swagger.yml", encoding = "utf-8")
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfig {
	
	//-------------swagger配置------------
	@Value("${title}")
	private String title;
	@Value("${description}")
	private String description;
	@Value("${version}")
	private String version;
	@Value("${termsOfServiceUrl}")
	private String termsOfServiceUrl;
	@Value("${contact}")
	private String contact;
	@Value("${license}")
	private String license;
	@Value("${licenseUrl}")
	private String licenseUrl;
	
	@Bean
    public Docket ProductApi() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false)
				.forCodeGeneration(false)
				.pathMapping("/")
				.select()
				.build()
				.apiInfo(productApiInfo());
    }

    private ApiInfo productApiInfo() {
    	return new ApiInfo(title,
        		description,
        		version,
        		termsOfServiceUrl,
        		contact,
        		license,
        		licenseUrl);
    }
}