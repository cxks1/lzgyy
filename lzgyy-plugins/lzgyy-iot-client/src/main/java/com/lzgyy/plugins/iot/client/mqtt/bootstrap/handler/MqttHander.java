package com.lzgyy.plugins.iot.client.mqtt.bootstrap.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

/**
 * mqtt协议处理器
 **/
@Slf4j
public abstract class MqttHander extends SimpleChannelInboundHandler<MqttMessage> {
	
	// 自定义 对外暴露 消息处理api
    MqttHandlerIntf mqttHandlerApi;

    public MqttHander(MqttHandlerIntf mqttHandlerIntf){
        this.mqttHandlerApi=mqttHandlerIntf;
    }
    
    /**
     * @param channelHandlerContext 允许ChannelHandler与其他的ChannelHandler实现进行交互。ChannelHandlerContext不会改变添加到其中的ChannelHandler，因此它是安全的。
     * @param mqttHander  mqtt协议处理器
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        Optional.ofNullable(mqttFixedHeader)
                .ifPresent(mqttFixedHeader1 -> doMessage(channelHandlerContext, mqttMessage));
    }

    public abstract void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage);

    // 断开通道
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("【DefaultMqttHandler：channelInactive】"+ctx.channel().localAddress().toString()+"关闭成功");
        mqttHandlerApi.close(ctx.channel());
        super.channelInactive(ctx);
    }
    
    // 重试次数
    private int UNCONNECT_NUM = 0;
    
    // 客户端的心跳实现客户端的断点重连工作
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        /*if(evt instanceof IdleStateEvent){
            mqttHandlerApi.doTimeOut(ctx.channel(),(IdleStateEvent)evt);
        }
        super.userEventTriggered(ctx, evt);*/
    	
    	if (evt instanceof IdleStateEvent) {
    		
    		if(UNCONNECT_NUM >= 4) {
    			// 此处当重启次数达到4次之后，关闭此链接后，并重新请求进行一次登录请求
        		System.err.println("连接状态失败了，重试了4次已经尽力了， sorry connect status is disconnect");
        		ctx.close();
        		return;
        	}
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                /*读超时*/
            	// 读取服务端消息超时时，直接断开该链接，并重新登录请求，建立通道
            	UNCONNECT_NUM++;
            	System.err.println("不好意思了，读取超时了，reader_idle over.");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                /*写超时*/   
            	System.out.println("发送心跳连接到服务端，send ping to server---date=" + new Date());
            	mqttHandlerApi.doTimeOut(ctx.channel(),(IdleStateEvent)evt);
            } else if (event.state() == IdleState.ALL_IDLE) {
                /*总超时*/
            	// 读取服务端消息超时时，直接断开该链接，并重新登录请求，建立通道
            	System.err.println("总超时，all_idle over.");
            	UNCONNECT_NUM++;
            }
        }
    }

}