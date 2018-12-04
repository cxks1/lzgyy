package com.lzgyy.plugins.iot.client.mqtt.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 链接参数配置
 **/
@ConfigurationProperties(prefix ="lzgyy.iot.client")
@Data
public class ConnectOptions {
	
	// 线程连接超时，单位秒
    private int connectTime;
    // 定时任务最小超时时间，单位秒
    private int minPeriod;
    
    // [netty] 服务器IP
    private String serverIp;
    // [netty] 服务器端口号
    private int serverPort;
    // [netty] 客户端端口号
    private int clientPort;
    // [netty] 是否保持连接检测对方主机是否崩溃
    private boolean isSoKeepalive ;
    // [netty] 地址复用，默认值False。有四种情况可以使用：(1).当有一个有相同本地地址和端口的socket1处于TIME_WAIT状态时，而你希望启动的程序的socket2要占用该地址和端口，比如重启服务且保持先前端口。(2).有多块网卡或用IP Alias技术的机器在同一端口启动多个进程，但每个进程绑定的本地IP地址不能相同。(3).单个进程绑定相同的端口到多个socket上，但每个socket绑定的ip地址不同。(4).完全相同的地址和端口的重复绑定。但这只用于UDP的多播，不用于TCP。
    private boolean isSoReuseaddr ;
    // [netty] TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
    private boolean isTcpNodelay ;
    // [netty] Socket参数，TCP数据发送缓冲区大小。
    private int soSndbuf;
    // [netty] Socket参数，TCP数据接收缓冲区大小。
    private int soRevbuf;
    // [netty] 连接超时毫秒数，默认值30000毫秒即30秒。
    private int connectTimeOutMillis;
    /**
     * [netty] IdleStateHandler心跳检测器,写超时时间，单位秒
     * 当一个写操作不能在一定的时间内完成时，抛出此异常，并关闭连接。你同样可以在 exceptionCaught 方法中处理这个异常
     */
    private int writerIdleTimeSeconds;
    
    // [ssl] 使用ssl加密
    private boolean isSsl;
    // [ssl] 加密 jks文件地址
    private String jksFile;
    // [ssl] 加密jks目标源存储库口令，storepass指定密钥库的密码
    private String jksStorePass;
    // [ssl] 加密jks目标密钥库口令
 	private String jksKeyPass;

    private MqttOpntions mqtt;
    
    @Data
    public static class MqttOpntions{
    	// [mqtt] 是否连接标识
        private boolean isWillFlag;
    	// [mqtt] 客户端标识符
        private String clientIdentifier;
        // [mqtt] 连接主题（isWillFlag为true时配置）
        private String willTopic;
        // [mqtt] 连接消息（isWillFlag为true时配置）
        private String willMessage;
        // [mqtt] 是否有用户名
        private boolean isHasUserName;
        // [mqtt] 是否有密码
        private boolean isHasPassword;
        // [mqtt] 用户名（isHasUserName为true时配置）
        private String userName;
        // [mqtt] 密码（isHasPassword为true时配置）
        private String password;
        // [mqtt] 是否保留遗嘱标志（isWillFlag为true时配置）
        private boolean isWillRetain;
        /**
         * 连接服务质量等级（isWillFlag为true时配置）
         * qos		连接服务质量等级  主要用于PUBLISH（发布态）消息的，保证消息传递的次数
         * 					0 最多一次的传输 即<=1  发送者只发送一次消息，不进行重试，Broker不会返回确认消息
    						1 至少一次的传输  即>=1
    						2 只有一次的传输 即==1
         */
        private int willQos;
        // 是否清除session
        private boolean isCleanSession;
        /**
         * 保持连接时间，连接允许的最大空闲时间，超过该时间服务端断开连接，单位秒
         * 保持连接（Keep Alive）是一个以秒为单位的时间间隔，表示为一个16位的字，它是指在客户端传输完成一个控制报文的时刻到发送下一个报文的时刻，
         * 两者之间允许空闲的最大时间间隔。
		 * 客户端负责保证控制报文发送的时间间隔不超过保持连接的值。如果没有任何其它的控制报文可以发送，客户端必须发送一个PINGREQ报文
         */
        private int KeepAliveTime;
        
        public boolean getIsHasUserName(){
        	return isHasUserName;
        }
        public void setIsHasUserName(boolean isHasUserName){
        	this.isHasUserName = isHasUserName;
        }
        public boolean getIsHasPassword(){
        	return isHasPassword;
        }
        public void setIsHasPassword(boolean isHasPassword){
        	this.isHasPassword = isHasPassword;
        }
        public boolean getIsWillRetain(){
        	return isWillRetain;
        }
        public void setIsWillRetain(boolean isWillRetain){
        	this.isWillRetain = isWillRetain;
        }
        public boolean getIsWillFlag(){
        	return isWillFlag;
        }
        public void setIsWillFlag(boolean isWillFlag){
        	this.isWillFlag = isWillFlag;
        }
        public boolean getIsCleanSession(){
        	return isCleanSession;
        }
        public void setIsCleanSession(boolean isCleanSession){
        	this.isCleanSession = isCleanSession;
        }
    }
    
    public boolean getIsSoKeepalive(){
    	return isSoKeepalive;
    }
    public void setIsSoKeepalive(boolean isSoKeepalive){
    	this.isSoKeepalive = isSoKeepalive;
    }
    public boolean getIsSoReuseaddr(){
    	return isSoReuseaddr;
    }
    public void setIsSoReuseaddr(boolean isSoReuseaddr){
    	this.isSoReuseaddr = isSoReuseaddr;
    }
    public boolean getIsTcpNodelay(){
    	return isTcpNodelay;
    }
    public void setIsTcpNodelay(boolean isTcpNodelay){
    	this.isTcpNodelay = isTcpNodelay;
    }
    public boolean getIsSsl(){
    	return isSsl;
    }
    public void setIsSsl(boolean isSsl){
    	this.isSsl = isSsl;
    }
}