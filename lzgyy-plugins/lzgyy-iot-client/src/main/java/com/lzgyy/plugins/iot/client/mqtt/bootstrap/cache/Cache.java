package com.lzgyy.plugins.iot.client.mqtt.bootstrap.cache;

import java.util.concurrent.ConcurrentHashMap;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SendMqttMessage;

/**
 * 缓存
 **/
public class Cache {

    private static  ConcurrentHashMap<Integer,SendMqttMessage> message = new ConcurrentHashMap<>();


    public static  boolean put(Integer messageId,SendMqttMessage mqttMessage){

        return message.put(messageId,mqttMessage)==null;

    }

    public static SendMqttMessage get(Integer messageId){

        return  message.get(messageId);

    }

    public static SendMqttMessage del(Integer messageId){
        return message.remove(messageId);
    }
}