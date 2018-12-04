package com.lzgyy.plugins.iot.client.mqtt.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lzgyy.plugins.iot.client.mqtt.auto.MqttListener;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SendMqttMessage;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.cache.Cache;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.channel.MqttHandlerServiceService;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.handler.DefaultMqttHandler;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.scan.SacnScheduled;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;
import com.lzgyy.plugins.iot.client.mqtt.enums.ConfirmStatus;
import com.lzgyy.plugins.iot.client.mqtt.ip.IpUtils;
import com.lzgyy.plugins.iot.client.mqtt.util.MessageId;

/**
 * 操作类
 **/
@Slf4j
public abstract class AbsMqttProducer extends MqttApi implements Producer {
	
	// 它是Netty网络通信的主体，由它负责同对端进行网络通信、注册和数据操作等功能
    protected Channel channel;
    // mqtt监听，返回
    protected MqttListener mqttListener;
    // 定义netty客户端引导类
    private  NettyBootstrapClient nettyBootstrapClient ;
    // 扫描消息确认
    protected SacnScheduled sacnScheduled;
    
    // 获得报文的消息ID和相关的订阅主题
    protected List<MqttTopicSubscription> topics = new ArrayList<>();

    // ountDownLatch是一个同步工具类，用来协调多个线程之间的同步，或者说起到线程之间的通信（而不是用作互斥的作用）。
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    protected void connectTo(ConnectOptions connectOptions){
        if(this.nettyBootstrapClient ==null){
            this.nettyBootstrapClient = new NettyBootstrapClient(connectOptions);
        }
        this.channel = nettyBootstrapClient.start();
        initPool(connectOptions.getMinPeriod());
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            nettyBootstrapClient.doubleConnect(); // 重新连接
        }
    }

    @Override
    public void disConnect() {
        sendDisConnect(channel);
    }

    @Override
    public void pubRecMessage(Channel channel, int messageId) {
        SendMqttMessage sendMqttMessage= SendMqttMessage.builder().messageId(messageId)
                .confirmStatus(ConfirmStatus.PUBREC)
                .timestamp(System.currentTimeMillis())
                .build();
        Cache.put(messageId,sendMqttMessage);
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(sendMqttMessage);
        } while (!flag);

        super.pubRecMessage(channel, messageId);
    }
    
    // 发送消息
    @Override
    protected void pubMessage(Channel channel, SendMqttMessage mqttMessage) {
        super.pubMessage(channel, mqttMessage);
        if(mqttMessage.getQos()!=0){
            Cache.put(mqttMessage.getMessageId(),mqttMessage);
            boolean flag;
            do {
                flag = sacnScheduled.addQueue(mqttMessage);
            } while (!flag);
        }
    }
    // 初始化池
    protected void initPool(int seconds){
        this.sacnScheduled = new SacnScheduled(this,seconds);
        sacnScheduled.start();
    }

    // 通过订阅标题，消息ID获得
    @Override
    protected void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {
    	// 开线程
        /*ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                super.subMessage(channel, mqttTopicSubscriptions, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        // 订阅消息返回给客户端
        super.subMessage(channel, mqttTopicSubscriptions, messageId);*/
    	
    	/*ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
            	channel.attr(getKey(Integer.toString(messageId)));
                super.subMessage(channel, mqttTopicSubscriptions, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);*/
    	
    	if(channel.isActive()){
        	channel.attr(getKey(Integer.toString(messageId)));
            super.subMessage(channel, mqttTopicSubscriptions, messageId);
        }
    }
    
    // 不订阅，断开
    public void unsub(List<String> topics,int messageId) {
    	// 开线程处理
        /*ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                unSubMessage(channel, topics, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        unSubMessage(channel, topics, messageId);*/
    	
    	if(channel.isActive()){
    		channel.attr(getKey(Integer.toString(messageId)));
            unSubMessage(channel, topics, messageId);
        }
    }

    @Override
    public void close() {
        if(nettyBootstrapClient!=null){
            nettyBootstrapClient.shutdown();
        }
        if(sacnScheduled!=null){
            sacnScheduled.close();
        }
    }
    
    // 连接返回消息
    public void connectBack(MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch (mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                countDownLatch.countDown();
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                throw new RuntimeException("用户名密码错误");
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                throw  new RuntimeException("clientId  不允许链接");
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                throw new RuntimeException("服务不可用");
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                throw new RuntimeException("mqtt 版本不可用");
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                throw new RuntimeException("未授权登录");
        }
        log.debug("CONNACK 连接回执，返回消息");
    }
    
    // 定义netty客户端引导类
    public class NettyBootstrapClient extends AbstractBootstrapClient {
    	/**
    	 * NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，每个线程负责处理多个Channel上的事件，
    	 * 而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
    	 */
        private NioEventLoopGroup bossGroup;
        /**
         * 引导程序，将ChannelPipeline、ChannelHandler、EventLoop进行整体关联作用
         */
        Bootstrap bootstrap = null ;// 启动辅助类
        
        // 链接参数配置
        private ConnectOptions connectOptions;

        public NettyBootstrapClient(ConnectOptions connectOptions) {
            this.connectOptions = connectOptions;
        }

        public void doubleConnect(){
            ChannelFuture connect = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getServerPort());
            connect.addListener((ChannelFutureListener) future -> {
                Thread.sleep(2000);
                if (future.isSuccess()){
                    AbsMqttProducer absMqttProducer = AbsMqttProducer.this;
                    absMqttProducer.channel =future.channel();
                    absMqttProducer.subMessage(future.channel(),topics, MessageId.messageId());
                }
                else {
                	doubleConnect();
                }
            });
        }
        @Override
        public Channel start() {
            initEventPool();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, connectOptions.getIsSoKeepalive())
                    .option(ChannelOption.TCP_NODELAY, connectOptions.getIsTcpNodelay())
                    // 连接超时毫秒数，默认值30000毫秒即30秒。
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectOptions.getConnectTimeOutMillis())
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_SNDBUF, connectOptions.getSoSndbuf())
                    .option(ChannelOption.SO_RCVBUF, connectOptions.getSoRevbuf())
                    .option(ChannelOption.SO_REUSEADDR, connectOptions.getIsSoReuseaddr())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initHandler(
                            			// 抽象数据管道
                            			ch.pipeline(),
                            			// 客户端配置参数
                            			connectOptions,
                            			// mqtt协议处理器
                            			new DefaultMqttHandler(connectOptions,new MqttHandlerServiceService(), AbsMqttProducer.this, mqttListener)
                            			);
                        }
                    });
            try {
            	bootstrap.localAddress(connectOptions.getClientPort());
                return bootstrap.connect(connectOptions.getServerIp(), connectOptions.getServerPort()).sync().channel();
            } catch (Exception e) {
                log.info("connect to channel fail ",e);
            }
            return null;
        }
        @Override
        public void shutdown() {
            if( bossGroup!=null ){
                try {
                    bossGroup.shutdownGracefully().sync();// 优雅关闭
                } catch (InterruptedException e) {
                    log.info("客户端关闭资源失败【" + IpUtils.getHost() + ":" + connectOptions.getServerPort() + "】");
                }
            }
        }

        @Override
        public void initEventPool() {
            bootstrap= new Bootstrap();
            bossGroup = new NioEventLoopGroup(4, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);
                public Thread newThread(Runnable r) {
                    return new Thread(r, "BOSS_" + index.incrementAndGet());
                }
            });
        }
    }

    public NettyBootstrapClient getNettyBootstrapClient() {
        return nettyBootstrapClient;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setMqttListener(MqttListener mqttListener) {
        this.mqttListener = mqttListener;
    }

}