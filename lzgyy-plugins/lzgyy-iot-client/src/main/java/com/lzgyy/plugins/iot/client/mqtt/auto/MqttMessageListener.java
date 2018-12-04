package com.lzgyy.plugins.iot.client.mqtt.auto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * 消费者配置注解类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageListener {

    String topic() ;

    MqttQoS qos() default MqttQoS.AT_MOST_ONCE;

}