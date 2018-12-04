package com.lzgyy.plugins.iot.service.mqtt.broker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzgyy.plugins.iot.service.mqtt.broker.codec.MqttWebSocketCodec;
import com.lzgyy.plugins.iot.service.mqtt.broker.config.BrokerProperties;
import com.lzgyy.plugins.iot.service.mqtt.broker.handler.MqttBrokerHandler;
import com.lzgyy.plugins.iot.service.mqtt.broker.protocol.ProtocolProcess;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * netty启动Broker服务
 */
@Component
public class MqttBrokerServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttBrokerServer.class);
	
	/**
	 * 服务配置
	 */
	@Autowired
	private BrokerProperties brokerProperties;
	
	/**
	 * 协议处理
	 */
	@Autowired
	private ProtocolProcess protocolProcess;	
	
	// 处理客户端的连接请求
	private EventLoopGroup bossGroup;
	// 处理I/O相关的读写操作
	private EventLoopGroup workerGroup;
	// [netty]安全套接字协议
	private SslContext sslContext;
	// [javax]安全套接字协议
	//private SSLContext sslServerContest;
	
	// 网络通信的主体，负责同对端进行网络通信、注册和数据操作等功能
	private Channel channel;

	private Channel websocketChannel;
	
	@PostConstruct
	public void start() throws Exception {
		LOGGER.info("Initializing {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
		// 初始化 接收客户端的TCP连接的bossGroup线程池
		bossGroup = brokerProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup(1);
		// 初始化 处理I/O相关的读写操作，或者执行系统Task、定时任务Task等的workerGroup线程池
		workerGroup = brokerProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		
		if (brokerProperties.getIsSsl()) {
			
			// 密钥库KeyStore
			KeyStore keyStore = KeyStore.getInstance("JKS");
			// 加载客户端证书
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(brokerProperties.getJksFile());
			//加载服务端的KeyStore，Netty是生成仓库时设置的密码，用于检查密钥库完整性的密码（密钥库的密码）
			keyStore.load(inputStream, brokerProperties.getJksStorePass().toCharArray());
			inputStream.close();
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			// 初始化密钥管理器
			kmf.init(keyStore, brokerProperties.getJksKeyPass().toCharArray());
			
			sslContext = SslContextBuilder.forServer(kmf).build();
			
			// 获取安全套接字协议（TLS协议）的对象
			//sslServerContest = SSLContext.getInstance("TLS");
			// 参数一：认证的密钥
			// 参数二：对等信任认证
			// 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
			//sslServerContest.init(kmf.getKeyManagers(), null, null);
		}
		
		// mqtt服务初始化
		mqttServer();
		// websocket服务初始化
		websocketServer();
		LOGGER.info("MQTT Broker {} is up and running. Open SSLPort: {} WebSocketSSLPort: {}", "[" + brokerProperties.getId() + "]", brokerProperties.getServerPort(), brokerProperties.getWebsocketServerPort());
	}
	
	@PreDestroy
	public void stop() {
		LOGGER.info("Shutdown {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
		bossGroup.shutdownGracefully();
		bossGroup = null;
		workerGroup.shutdownGracefully();
		workerGroup = null;
		channel.closeFuture().syncUninterruptibly();
		channel = null;
		websocketChannel.closeFuture().syncUninterruptibly();
		websocketChannel = null;
		LOGGER.info("MQTT Broker {} shutdown finish.", "[" + brokerProperties.getId() + "]");
	}
	
	/**
	 * mqtt服务
	 * @throws Exception
	 */
	private void mqttServer() throws Exception {

		ServerBootstrap sb = new ServerBootstrap();
		// 设置 ServerBootstrap 要用的 EventLoopGroup,这个 EventLoopGroup 将用于 ServerChannel 和被接受的子 Channel 的 I/O 处理
		sb.group(bossGroup, workerGroup)
			.channel(brokerProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline channelPipeline = socketChannel.pipeline();
					// Netty提供的心跳检测
					channelPipeline.addFirst("idle", new IdleStateHandler(brokerProperties.getReaderIdleTimeSeconds(), 0, 0));
					if (brokerProperties.getIsSsl()) {
						// Netty提供的SSL处理
						SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
						//SSLEngine sslEngine = sslServerContest.createSSLEngine();
						sslEngine.setUseClientMode(false);      // 设置为服务器模式
						//sslEngine.setNeedClientAuth(false);      // 不需要客户端认证，默认为false，故不需要写这行
						channelPipeline.addLast("ssl", new SslHandler(sslEngine));
					}
					channelPipeline.addLast("decoder", new MqttDecoder());
					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
					channelPipeline.addLast("broker", new MqttBrokerHandler(protocolProcess));
				}
			})
			.option(ChannelOption.SO_BACKLOG, brokerProperties.getSoBacklog())
			.childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());
		
		channel = sb.bind(brokerProperties.getServerPort()).sync().channel();
	}
	
	/**
	 * websocket服务
	 * @throws Exception
	 */
	private void websocketServer() throws Exception {
		
		ServerBootstrap sb = new ServerBootstrap();
		sb.group(bossGroup, workerGroup)
			.channel(brokerProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline channelPipeline = socketChannel.pipeline();
					channelPipeline.addFirst("idle", new IdleStateHandler(brokerProperties.getReaderIdleTimeSeconds(), 0, 0));
					if (brokerProperties.getIsSsl()) {
						SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
						sslEngine.setUseClientMode(false);         // 设置为服务器模式
						//sslEngine.setNeedClientAuth(false);      // 不需要客户端认证，默认为false，故不需要写这行
						channelPipeline.addLast("ssl", new SslHandler(sslEngine));
					}
					// 将请求和应答消息编码或解码为HTTP消息
					channelPipeline.addLast("http-codec", new HttpServerCodec());
					// 将HTTP消息的多个部分合成一条完整的HTTP消息
					channelPipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
					// 将HTTP消息进行压缩编码
					channelPipeline.addLast("compressor ", new HttpContentCompressor());
					// netty支持websocket
					// 处理除TextWebSocketFrame以外的消息事件。所有规定的WebSocket帧类型
					channelPipeline.addLast("protocol", new WebSocketServerProtocolHandler(brokerProperties.getWebsocketPath(), "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
					channelPipeline.addLast("mqttWebSocket", new MqttWebSocketCodec());
					channelPipeline.addLast("decoder", new MqttDecoder());
					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
					channelPipeline.addLast("broker", new MqttBrokerHandler(protocolProcess));
				}
			})
			.option(ChannelOption.SO_BACKLOG, brokerProperties.getSoBacklog())
			.childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());
		websocketChannel = sb.bind(brokerProperties.getWebsocketServerPort()).sync().channel();
	}

}