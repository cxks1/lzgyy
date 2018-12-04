package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.APubAck;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;

/**
 * 发布回执
 */
public class PubAck extends APubAck<MqttMessageIdVariableHeader>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PubAck.class);
	
	public PubAck(IMessageIdService messageIdService, IDupPublishMessageStoreService dupPublishMessageStoreService) {
		super(messageIdService, dupPublishMessageStoreService);
	}
	
	@Override
	public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		LOGGER.debug("PUBACK - clientIp: {}, clientId: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
		dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
		messageIdService.releaseMessageId(messageId);
	}

}