package com.lzgyy.plugins.iot.service.mqtt.broker.internal;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;

import org.apache.ignite.IgniteMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzgyy.plugins.iot.core.broker.internal.bean.InternalMessage;
import com.lzgyy.plugins.iot.core.broker.internal.service.IInternalCommunication;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.bean.SubscribeStore;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 内部通信, 基于发布-订阅范式
 */
@Component
public class InternalCommunication implements IInternalCommunication{

	private static final Logger LOGGER = LoggerFactory.getLogger(InternalCommunication.class);

	private final String internalTopic = "internal-communication-topic";
	
	/**
	 * 分布式消息传递
	 */
	@Autowired
	private IgniteMessaging igniteMessaging;
	
	/**
	 * 会话存储服务接口
	 */
	@Autowired
	private ISessionStoreService sessionStoreService;
	
	/**
	 * 订阅存储服务接口
	 */
	@Autowired
	private ISubscribeStoreService subscribeStoreService;
	
	/**
	 * 分布式生成报文标识符接口
	 */
	@Autowired
	private IMessageIdService messageIdService;

	@PostConstruct
	public void internalListen() {
		igniteMessaging.localListen(internalTopic, (nodeId, msg) -> {
			InternalMessage internalMessage = (InternalMessage) msg;
			this.sendPublishMessage(internalMessage.getTopic(), MqttQoS.valueOf(internalMessage.getMqttQoS()), internalMessage.getMessageBytes(), internalMessage.isRetain(), internalMessage.isDup());
			return true;
		});
	}

	public void internalSend(InternalMessage internalMessage) {
		if (igniteMessaging.clusterGroup().nodes() != null && igniteMessaging.clusterGroup().nodes().size() > 0) {
			igniteMessaging.send(internalTopic, internalMessage);
		}
	}

	private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
		List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
		subscribeStores.forEach(subscribeStore -> {
			if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
				// 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
				MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
				if (respQoS == MqttQoS.AT_MOST_ONCE) {
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.AT_LEAST_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.EXACTLY_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
			}
		});
	}

}