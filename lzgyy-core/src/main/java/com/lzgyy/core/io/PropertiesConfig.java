/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.lzgyy.core.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;

import com.lzgyy.common.io.IOUtils;
import com.lzgyy.common.io.ResourceUtils;
import com.lzgyy.common.lang.ObjectUtils;
import com.lzgyy.common.lang.StringUtils;

/**
 * PropertiesConfig配置类， 可载入多个properties、yml文件，
 * 相同的属性在最后载入的文件中的值将会覆盖之前的值， 
 * 取不到从System.getProperty()获取。
 * @author ThinkGem
 * @version 2017-12-30
 */
public class PropertiesConfig {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);
	
	private final Properties properties = new Properties();
	
	/**
	 * 载入多个文件，路径使用Spring Resource格式，相同的属性在最后载入的文件中的值将会覆盖之前的值。
	 */
	public PropertiesConfig(String... configFiles) {
		logger.info("正在加载配置文件.....");
		for (String location : configFiles) {
			try {
				Resource resource = ResourceUtils.getResource(location);
				if (resource.exists()){
					
					if ((location == null) || (location.lastIndexOf(".") == -1) 
						|| (location.lastIndexOf(".") == location.length() - 1)) {
						logger.info("<<<<<<<<<<<< 亲,配置文件未找到 ^_^ >>>>>>>>");
						System.err.println();
					}
					
        			String ext = StringUtils.lowerCase(location.substring(location.lastIndexOf(".") + 1));
        			if ("properties".equals(ext)){
        				
        				InputStreamReader is = null;
        				try {
	    					is = new InputStreamReader(resource.getInputStream(), "UTF-8");
	    					properties.load(is);
	    					
	    					if(StringUtils.isNotBlank(properties.getProperty("spring.profiles.active"))){
	    						Resource[] resources2 = ResourceUtils.getResources("classpath*:/config/application-"+ properties.getProperty("spring.profiles.active") +".properties");
	    						for(Resource resource2 : resources2){
	    							is = new InputStreamReader(resource2.getInputStream(), "UTF-8");
	    	    					properties.load(is);
	    						}
	    					}
	    					
	    					if(StringUtils.isNotBlank(properties.getProperty("spring.other.config"))){
	    						Resource[] resources2 = ResourceUtils.getResources("classpath*:/config/"+ properties.getProperty("spring.other.config") +".properties");
	    						for(Resource resource2 : resources2){
	    							is = new InputStreamReader(resource2.getInputStream(), "UTF-8");
	    	    					properties.load(is);
	    						}
	    					}
	    					
	        			} catch (IOException ex) {
	            			logger.error("Load " + location + " failure. ", ex);
	        			} finally {
	        				IOUtils.closeQuietly(is);
	        			}
        				
        			} else if ("yml".equals(ext)){
        				
        				YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        				bean.setResources(resource);
        				InputStreamReader is = null;
        				for (Map.Entry<Object,Object> entry : bean.getObject().entrySet()){
        					properties.put(ObjectUtils.toString(entry.getKey()),
        							ObjectUtils.toString(entry.getValue()));
        					if ("spring.profiles.active".equals(entry.getKey())) {
        						Resource[] resources2 = ResourceUtils.getResources("classpath*:/config/application-"+ properties.getProperty("spring.profiles.active") +".yml");
	    						for(Resource resource2 : resources2){
	    							is = new InputStreamReader(resource2.getInputStream(), "UTF-8");
	    	    					properties.load(is);
	    						}
        					}
        					if(StringUtils.isNotBlank(properties.getProperty("spring.other.config"))){
	    						Resource[] resources2 = ResourceUtils.getResources("classpath*:/config/"+ properties.getProperty("spring.other.config") +".yml");
	    						for(Resource resource2 : resources2){
	    							is = new InputStreamReader(resource2.getInputStream(), "UTF-8");
	    	    					properties.load(is);
	    						}
	    					}
        				}
        				
        			}
				}
			} catch (Exception e) {
    			logger.error("Load " + location + " failure. ", e);
			}
		}
		logger.info("完成加载配置文件.");
	}
	
	/**
	 * 获取当前加载的属性
	 */
	public Properties getProperties() {
		return properties;
	}
	
	// 正则表达式预编译
	private static Pattern p1 = Pattern.compile("\\$\\{.*?\\}");

	/**
	 * 获取属性值，取不到从System.getProperty()获取，都取不到返回null
	 */
	public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null){
        	// 支持嵌套取值的问题  key=${xx}/yy
	    	Matcher m = p1.matcher(value);
	        while(m.find()) {
	            String g = m.group();
	            String keyChild = g.replaceAll("\\$\\{", "").replaceAll("\\}", "");
	            value = value.replace(g, getProperty(keyChild));
	        }
	        return value;
	    }else{
	    	String systemProperty = System.getProperty(key);
			if (systemProperty != null) {
				return systemProperty;
			}
	    }
		return null;
	}

	/**
	 * 取出String类型的Property，但以System的Property优先，如果都为null则返回defaultValue值
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return value != null ? value : defaultValue;
	}
	
}
