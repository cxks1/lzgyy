package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.APubComp;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;

/**
 * QoS2消息完成处理
 */
public class PubComp extends APubComp<MqttMessageIdVariableHeader>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PubComp.class);

	public PubComp(IMessageIdService messageIdService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		super(messageIdService, dupPubRelMessageStoreService);
	}
	
	@Override
	public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		LOGGER.debug("PUBCOMP - clientIp: {}, clientId: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
		dupPubRelMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		messageIdService.releaseMessageId(messageId);
	}
}