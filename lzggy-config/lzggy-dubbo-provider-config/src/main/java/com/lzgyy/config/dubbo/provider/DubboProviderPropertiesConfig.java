package com.lzgyy.config.dubbo.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *	dubbo生产者配置
 */
@ConfigurationProperties(prefix = "dubbo")
public class DubboProviderPropertiesConfig {

	/**
	 * 注册地址
	 */
	private String registryAddress;
	
	/**
	 * 注册客户端
	 */
	private String registryClient;
	
	/**
	 * 注册协议
	 */
	private String registryProtocol;
	
	/**
	 * 应用名称
	 */
	private String applicationName;
	
	/**
	 * 应用维护者
	 */
	private String applicationOwner;
	
	/**
	 * dubbo 绑定的端口号
	 */
	private Integer port;
	
	
	public String getRegistryAddress() {
		return registryAddress;
	}
	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	public String getRegistryClient() {
		return registryClient;
	}
	public void setRegistryClient(String registryClient) {
		this.registryClient = registryClient;
	}
	public String getRegistryProtocol() {
		return registryProtocol;
	}
	public void setRegistryProtocol(String registryProtocol) {
		this.registryProtocol = registryProtocol;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getApplicationOwner() {
		return applicationOwner;
	}
	public void setApplicationOwner(String applicationOwner) {
		this.applicationOwner = applicationOwner;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
}