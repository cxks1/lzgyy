package com.lzgyy.plugins.iot.client;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lzgyy.plugins.iot.client.mqtt.auto.MqttListener;
import com.lzgyy.plugins.iot.client.mqtt.auto.MqttMessageListener;

@Slf4j
@Service
@MqttMessageListener(qos = MqttQoS.AT_LEAST_ONCE, topic = "/t1/t2")
public class SubListener implements MqttListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubListener.class);
	
    @Override
    public void callBack(String topic, String msg) {
    	LOGGER.debug("============================="+topic+msg);
    }

    @Override
    public void callThrowable(Throwable e) {
    	LOGGER.debug("exception",e);
    }
}