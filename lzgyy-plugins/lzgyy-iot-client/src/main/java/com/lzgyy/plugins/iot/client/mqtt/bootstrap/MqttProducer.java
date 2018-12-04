package com.lzgyy.plugins.iot.client.mqtt.bootstrap;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SendMqttMessage;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SubMessage;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;
import com.lzgyy.plugins.iot.client.mqtt.enums.ConfirmStatus;
import com.lzgyy.plugins.iot.client.mqtt.util.MessageId;

/**
 * mqtt api操作类
 **/
@Slf4j
public class MqttProducer extends AbsMqttProducer{

	// 连接
    public Producer connect(ConnectOptions connectOptions){
        connectTo(connectOptions);
        return this;
    }
    
    // 推送
    @Override
    public void pub(String topic,String message,int qos){
        pub(topic,message,false,qos);
    }
    // 推送
    @Override
    public void pub(String topic, String message, boolean retained) {
        pub(topic,message,retained,0);
    }
    // 推送
    @Override
    public void pub(String topic,String message){
        pub(topic,message,false,0);
    }

    // 推送
    @Override
    public void pub(String topic, String message, boolean retained, int qos) {
    	// ifPresent 如果存在
        Optional.ofNullable(buildMqttMessage(topic, message, retained, qos, false, true))
        	.ifPresent(sendMqttMessage -> {
        		pubMessage(channel, sendMqttMessage);
        	});
    }
    
    // 构建Mqtt消息
    /**
     * @param topic		主题
     * @param message	消息
     * @param retained  主要用于PUBLISH(发布态)的消息，表示服务器要保留这次推送的信息，
     * 					如果有新的订阅者出现，就把这消息推送给它。如果不设那么推送至当前订阅的就释放了。
     * @param qos		服务质量等级  主要用于PUBLISH（发布态）消息的，保证消息传递的次数
     * 					00表示最多一次 即<=1  发送者只发送一次消息，不进行重试，Broker不会返回确认消息
						01表示至少一次  即>=1
						10表示一次，即==1
						11保留后用
     * @param dup		其是用来在保证消息传输可靠的，如果设置为1，则在下面的变长头部里多加MessageId,并需要回复确认，
     * 					保证消息传输完成，但不能用于检测消息重复发送
     * @param time		
     * @return
     */
    private SendMqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos, boolean dup, boolean time) {
        int messageId=0;
        if(qos!=0){
            messageId = MessageId.messageId();
        }
        try {
            return SendMqttMessage.builder().messageId(messageId)
                    .Topic(topic)
                    .dup(dup)
                    .retained(retained)
                    .qos(qos)
                    .confirmStatus(ConfirmStatus.PUB)
                    .timestamp(System.currentTimeMillis())
                    .payload(message.getBytes("Utf-8")).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 订阅
    @Override
    public void sub(SubMessage... subMessages){
        Optional.ofNullable(getSubTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            int messageId = MessageId.messageId();
            // 通过订阅标题，消息ID获得
            subMessage(channel, mqttTopicSubscriptions, messageId);
            topics.addAll(mqttTopicSubscriptions);
        });
    }
    
    // 不订阅
    @Override
    public void unsub(List<String> topics) {
        Optional.ofNullable(topics).ifPresent(strings -> {
            int messageId = MessageId.messageId();
            super.unsub(strings,messageId);
        });
    }
    // 所有的都不订阅
    @Override
    public void unsub(){
        unsub(toList());
    }

    // 获得订阅标题列表
    private List<MqttTopicSubscription> getSubTopics(SubMessage[]subMessages ) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<MqttTopicSubscription> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        MqttTopicSubscription mqttTopicSubscription  = new MqttTopicSubscription(sb.getTopic(),sb.getQos());
                        mqttTopicSubscriptions.add(mqttTopicSubscription);
                    }
                    return mqttTopicSubscriptions;
                }).orElse(null);
    }

    private List<String> toList(){
        return Optional.ofNullable(topics).
                map(mqttTopicSubscriptions ->
                        mqttTopicSubscriptions.stream().
                                map(mqttTopicSubscription -> mqttTopicSubscription.topicName()).collect(Collectors.toList()))
                .orElse(null);
    }

    private List<String> getTopics(SubMessage[] subMessages) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<String> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        mqttTopicSubscriptions.add(sb.getTopic());
                    }
                    return mqttTopicSubscriptions;
                }).orElse(null);
    }

}