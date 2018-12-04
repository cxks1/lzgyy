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

import com.lzgyy.plugins.iot.core.broker.protocol.abs.APubRec;
import com.lzgyy.plugins.iot.core.store.message.bean.DupPubRelMessageStore;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;

/**
 * QoS2消息回执处理
 */
public class PubRec extends APubRec<MqttMessageIdVariableHeader>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PubRel.class);
	
	protected PubRec(IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		super(dupPublishMessageStoreService, dupPubRelMessageStoreService);
	}

	@Override
	public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		LOGGER.debug("PUBREC - clientIp: {}, clientId: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore().setClientId((String) channel.attr(AttributeKey.valueOf("clientId")).get())
			.setMessageId(variableHeader.messageId());
		dupPubRelMessageStoreService.put((String) channel.attr(AttributeKey.valueOf("clientId")).get(), dupPubRelMessageStore);
		channel.writeAndFlush(pubRelMessage);
	}

}