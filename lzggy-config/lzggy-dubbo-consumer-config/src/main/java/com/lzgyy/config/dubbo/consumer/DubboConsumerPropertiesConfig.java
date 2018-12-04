package com.lzgyy.config.dubbo.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *	dubbo消费者配置
 */
@ConfigurationProperties(prefix = "dubbo")
public class DubboConsumerPropertiesConfig {

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
	 * 超时
	 */
	private Integer timeout;
	
	private boolean check;
	
	
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
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
}