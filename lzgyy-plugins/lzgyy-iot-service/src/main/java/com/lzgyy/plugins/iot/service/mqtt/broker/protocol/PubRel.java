package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.APubRel;

/**
 * QoS2消息释放连接处理
 */
public class PubRel extends APubRel<MqttMessageIdVariableHeader>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PubRel.class);
	
	@Override
	public void processPubRel(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		LOGGER.debug("PUBREL - clentIp: {}, clientId: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		channel.writeAndFlush(pubCompMessage);
	}

}