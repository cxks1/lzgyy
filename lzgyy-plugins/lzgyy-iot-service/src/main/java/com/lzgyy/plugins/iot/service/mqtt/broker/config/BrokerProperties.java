package com.lzgyy.plugins.iot.service.mqtt.broker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 服务配置
 */
@ConfigurationProperties(prefix = "spring.mqtt.broker")
@Data
public class BrokerProperties {

	// Broker唯一标识
	private String id;

	// [netty]服务器端口号
	private int serverPort = 8883;

	// [netty]WebSocket服务器端口号
	private int websocketServerPort = 9993;

	// [netty]WebSocket Path值, 默认值 /mqtt
	private String websocketPath = "/mqtt";
	
	// [netty] IdleStateHandler心跳检测器,读超时时间，单位秒，当一个写操作不能在一定的时间内完成时，抛出此异常，并关闭连接。你同样可以在 exceptionCaught 方法中处理这个异常
	private int readerIdleTimeSeconds = 60;

	// [netty] 是否开启Epoll模式, 默认关闭
	private boolean useEpoll = false;

	// [netty] Sokcet参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
	private int soBacklog = 511;

	// [netty] Socket参数, 是否开启心跳保活机制, 默认开启
	private boolean soKeepAlive = true;
	
	// [ssl] 使用ssl加密
	private boolean isSsl;
	
	// [ssl] 加密 jks文件地址
	private String jksFile;
	
	// [ssl] 加密jks目标源存储库口令，storepass指定密钥库的密码
	private String jksStorePass;
	
	// [ssl] 加密jks目标密钥库口令
	private String jksKeyPass;
	
	// [ssl] 权限私钥加密路径（供加密用户密码使用）
	private String authPrivateKeyFile;
	
	public boolean getIsSsl(){
		return isSsl;
	}
	public void setIsSsl(boolean isSsl){
		this.isSsl = isSsl;
	}
	
}