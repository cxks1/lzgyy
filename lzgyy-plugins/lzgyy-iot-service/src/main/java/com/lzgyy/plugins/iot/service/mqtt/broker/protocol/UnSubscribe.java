package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.AUnSubscribe;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import java.util.List;

/**
 * 取消订阅
 */
public class UnSubscribe extends AUnSubscribe<MqttUnsubscribeMessage>{

	private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribe.class);
	
	public UnSubscribe(ISubscribeStoreService subscribeStoreService) {
		super(subscribeStoreService);
	}

	@Override
	public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
		List<String> topicFilters = msg.payload().topics();
		String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		topicFilters.forEach(topicFilter -> {
			subscribeStoreService.remove(topicFilter, clinetId);
			LOGGER.debug("UNSUBSCRIBE - clientIp: {}, clientId: {}, topicFilter: {}",channel.remoteAddress().toString(), clinetId, topicFilter);
		});
		MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
		channel.writeAndFlush(unsubAckMessage);
	}

}