package com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

/**
 * 订阅消息
 **/
@Builder
@Data
public class SubMessage {
	
	// 标题
    private String topic;
    
   /**服务质量等级  主要用于PUBLISH（发布态）消息的，保证消息传递的次数
    * 					00表示最多一次 即<=1  发送者只发送一次消息，不进行重试，Broker不会返回确认消息
						01表示至少一次  即>=1
						10表示一次，即==1
						11保留后用
	*/
    private MqttQoS qos;

}