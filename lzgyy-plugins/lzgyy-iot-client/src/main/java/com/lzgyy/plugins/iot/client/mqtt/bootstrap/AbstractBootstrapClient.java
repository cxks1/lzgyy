package com.lzgyy.plugins.iot.client.mqtt.bootstrap;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.handler.MqttHander;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;
import com.lzgyy.plugins.iot.client.mqtt.ssl.SecureSokcetTrustManagerFactory;

/**
 * 客户端引导抽象类
 **/
public abstract class AbstractBootstrapClient implements BootstrapClient {

    private SSLContext CLIENT_CONTEXT;
    
    // 安全套接字协议
 	private SslContext sslContext;

    private String PROTOCOL = "TLS";

    /** 初始化方法
     * @param channelPipeline  抽象数据管道
     * @param clientBean  客户端配置参数
     * @param mqttHander  mqtt协议处理器
     */
    protected void initHandler(ChannelPipeline channelPipeline, ConnectOptions clientBean, MqttHander mqttHander){
        if(clientBean.getIsSsl()){
            initSsl(clientBean);
            SSLEngine engine = sslContext.newEngine(channelPipeline.channel().alloc());
            //SSLEngine engine = CLIENT_CONTEXT.createSSLEngine();
            engine.setUseClientMode(true); // 客户方模式
            channelPipeline.addLast("ssl", new SslHandler(engine));
        }
        // 解码器  QTTDecoder和MqttDecoder直接把MQTT报文二进制转换成Packet对象，直接对对象处理就简单多了
        channelPipeline.addLast("decoder", new MqttDecoder());
        // 编码器
        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
        // 添加IdleStateHandler心跳检测处理器
        channelPipeline.addLast(new IdleStateHandler(0, clientBean.getWriterIdleTimeSeconds(), 0));
        // 添加 mqtt协议处理器
        channelPipeline.addLast(mqttHander);
    }

    private void initSsl(ConnectOptions clientBean){
        SSLContext clientContext;
        try {
        	// 密钥库
            KeyStore keyStore = KeyStore.getInstance("JKS");
            //加载客户端证书
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(clientBean.getJksFile());
            keyStore.load(inputStream, clientBean.getJksStorePass().toCharArray());
            
            // 信任库 
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            // 初始化信任库  
            tmf.init(keyStore);
            
            sslContext = SslContextBuilder.forClient().trustManager(tmf).build();
            
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, tmf.getTrustManagers(), null);
            
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
        }
        CLIENT_CONTEXT = clientContext;
    }
}