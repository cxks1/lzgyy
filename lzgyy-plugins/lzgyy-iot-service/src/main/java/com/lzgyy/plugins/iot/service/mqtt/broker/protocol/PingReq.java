package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.APingReq;

/**
 * PING请求连接处理
 */
public class PingReq extends APingReq<MqttMessage>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PingReq.class);
	
	@Override
	public void processPingReq(Channel channel, MqttMessage msg) {
		
		if (channel.isOpen() && channel.isActive() && channel.isWritable()) {
			MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
				LOGGER.debug("PINGREQ - clientIp: {}, clientId: {}", channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get());
				channel.writeAndFlush(pingRespMessage);
		}
	}

}