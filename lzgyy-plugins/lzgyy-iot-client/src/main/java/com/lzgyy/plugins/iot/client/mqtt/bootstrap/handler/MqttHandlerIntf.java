package com.lzgyy.plugins.iot.client.mqtt.bootstrap.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义 对外暴露 消息处理api
 **/
public interface MqttHandlerIntf {
	
	// 关闭
    void close(Channel channel);
    // 发送应答
    void puback(Channel channel, MqttMessage mqttMessage);

    void pubrec(Channel channel, MqttMessage mqttMessage);

    void pubrel(Channel channel, MqttMessage mqttMessage);

    void pubcomp(Channel channel, MqttMessage mqttMessage);
    // 超时
    void doTimeOut(Channel channel, IdleStateEvent evt);

}