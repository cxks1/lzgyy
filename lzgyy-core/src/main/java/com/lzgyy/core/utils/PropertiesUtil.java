package com.lzgyy.core.utils;

import com.lzgyy.core.io.PropertiesConfig;

/**
 * 配置工具类
 */
public class PropertiesUtil {
	
	/**
	 * 公共配置文件
	 */
	private static PropertiesConfig propertiesLoader = null;
	
	// 此地方后续改动
	public static void setPropertiesConfig (PropertiesConfig propertiesLoader) {
		PropertiesUtil.propertiesLoader = propertiesLoader;
	}
	
	public static String getProperty(String key) {
		return propertiesLoader.getProperty(key);
	}
	
	public static Integer getInteger(String key) {
		return Integer.valueOf(propertiesLoader.getProperty(key));
	}
	
	public static long getLong(String key) {
		return Long.valueOf(propertiesLoader.getProperty(key));
	}
}