package com.lzgyy.config.druid;

//import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.util.StringUtils;

/**
 *  设置druid 登录的用户名和密码以及ip的黑白名单
 *  通过以下地址访问druid管理页面
 * 	http://ip:8080/druid/index.html
 */
@Configuration
@EnableConfigurationProperties(DruidPropertiesConfig.class)
public class DruidConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(DruidConfiguration.class);
	
	@Autowired
	private DruidPropertiesConfig druidPropertiesConfig;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean statViewServlet() {
		logger.info("***** 设置 druid 管理账号...");
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
		// 白名单：
		servletRegistrationBean.addInitParameter("allow","");
		// IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的即提示:Sorry, you are not
		// permitted to view this page.
		//servletRegistrationBean.addInitParameter("deny", "127.0.0.1");
		if (!StringUtils.isEmpty(druidPropertiesConfig.getWhitelist())) {
			servletRegistrationBean.addInitParameter("deny", druidPropertiesConfig.getWhitelist());
		}
		// 登录查看信息的账号密码.
		servletRegistrationBean.addInitParameter("loginUsername", druidPropertiesConfig.getUsername());
		servletRegistrationBean.addInitParameter("loginPassword", druidPropertiesConfig.getPassword());
		// 是否能够重置数据.
		servletRegistrationBean.addInitParameter("resetEnable", druidPropertiesConfig.getResetEnable());
		servletRegistrationBean.addInitParameter("logSlowSql", druidPropertiesConfig.getLogSlowSql());
		
		return servletRegistrationBean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean statFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		
		filterRegistrationBean.setFilter(new WebStatFilter());
		// 添加过滤规则.
		filterRegistrationBean.addUrlPatterns("/*");
		// 添加不需要忽略的格式信息.
		filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}

	// 配置数据库的基本链接信息
	/*@Bean(name = "dataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	// 可以在application.properties中直接导入
	public DataSource dataSource() {
		logger.info("druid datasource configure ...");
		return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class).build();
	}*/
}