package com.lzgyy.platform;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.lzgyy.core.io.PropertiesConfig;
import com.lzgyy.core.utils.PropertiesUtil;


@SpringBootApplication
// 扫描包
@ComponentScan(basePackages={"com.lzgyy.platform"})
// 加载插件
@ImportResource(locations={"classpath:applicationContext-plug.xml"})
public class PlatformServiceProviderApp  extends SpringBootServletInitializer{

	private static final Logger logger = LoggerFactory.getLogger(PlatformServiceProviderApp.class);
	
	// 默认加载的文件，可通过继承覆盖（若有相同Key，优先加载后面的）
	public static final String[] DEFAULT_CONFIG_FILE = new String[]{"classpath:config/application.yml"};
	
    public static void main(String[] args) {
    	logger.info("***** PlatformServiceProviderApp starting");
    	System.setProperty("user.timezone","Asia/Shanghai"); //设置时区
    	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		SpringApplication app = new SpringApplication(PlatformServiceProviderApp.class);
		PropertiesConfig propertiesConfig = new PropertiesConfig(DEFAULT_CONFIG_FILE);
		app.setDefaultProperties(propertiesConfig.getProperties());
		PropertiesUtil.setPropertiesConfig(propertiesConfig); // 设置配置文件工具类
		app.run(args);
		logger.info("***** PlatformServiceProviderApp started");
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		this.setRegisterErrorPageFilter(false); // 错误页面由容器来处理，而不是SpringBoot
		PropertiesConfig propertiesConfig = new PropertiesConfig(DEFAULT_CONFIG_FILE);
		builder.properties(propertiesConfig.getProperties());
		PropertiesUtil.setPropertiesConfig(propertiesConfig); // 设置配置文件工具类
		return builder.sources(PlatformServiceProviderApp.class);
	}
}	 		