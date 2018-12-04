package com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean;

import com.lzgyy.plugins.iot.client.mqtt.enums.ConfirmStatus;

import lombok.Builder;
import lombok.Data;

/**
 * 发送MQTT消息对象
 **/
@Data
@Builder
public class SendMqttMessage {
	
	// 主题
    private String Topic;
    
    // 息体（Payload），存在于部分MQTT数据包中，表示客户端收到的具体内容
    private byte[] payload;
    
    /**
     * qos		服务质量等级  主要用于PUBLISH（发布态）消息的，保证消息传递的次数
     * 					00表示最多一次 即<=1  发送者只发送一次消息，不进行重试，Broker不会返回确认消息
						01表示至少一次  即>=1
						10表示一次，即==1
						11保留后用
     */
    private int qos;
    
    /**
     * 主要用于PUBLISH(发布态)的消息，表示服务器要保留这次推送的信息，
     * 如果有新的订阅者出现，就把这消息推送给它。如果不设那么推送至当前订阅的就释放了。
     */
    private boolean retained;
    
    /**
     * 其是用来在保证消息传输可靠的，如果设置为1，则在下面的变长头部里多加MessageId,并需要回复确认，
     * 保证消息传输完成，但不能用于检测消息重复发送
     */
    private boolean dup;

    private int messageId;

    private long timestamp;

    private volatile ConfirmStatus confirmStatus;

}