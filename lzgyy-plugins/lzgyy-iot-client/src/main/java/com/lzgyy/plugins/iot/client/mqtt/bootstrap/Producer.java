package com.lzgyy.plugins.iot.client.mqtt.bootstrap;

import io.netty.channel.Channel;
import java.util.List;

import com.lzgyy.plugins.iot.client.mqtt.auto.MqttListener;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SubMessage;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;

/**
 * 生产者接口定义
 * 发布/订阅消息接口
 **/
public interface Producer {

    Channel getChannel();
    
    Producer connect(ConnectOptions connectOptions);

    void close();

    void setMqttListener(MqttListener mqttListener);

    void pub(String topic,String message,boolean retained,int qos);

    void pub(String topic,String message);

    void pub(String topic,String message,int qos);

    void pub(String topic,String message,boolean retained);

    void sub(SubMessage... subMessages);

    void unsub(List<String> topics);

    void unsub();

    void disConnect();

}