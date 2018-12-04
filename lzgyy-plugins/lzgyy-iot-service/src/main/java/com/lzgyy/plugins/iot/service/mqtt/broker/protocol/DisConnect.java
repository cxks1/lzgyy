package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.ADisConnect;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.bean.SessionStore;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;
import com.lzgyy.plugins.iot.service.mqtt.store.message.service.impl.RetainMessageStoreService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;

/**
 * 断开连接
 */
public class DisConnect extends ADisConnect<MqttMessage>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DisConnect.class);

	protected DisConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		super(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService);
	}

	@Override
	public void processDisConnect(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		SessionStore sessionStore = sessionStoreService.get(clientId);
		if (sessionStore.isCleanSession()) {
			
			//subscribeStoreService.destroy();
			//dupPublishMessageStoreService.destroy();
			//dupPubRelMessageStoreService.destroy();
			
			// 订阅存储服务删除
			subscribeStoreService.removeForClient(clientId);
			LOGGER.debug("断开连接 删除订阅存储 - clientIp: {}, clientId: {}",channel.remoteAddress().toString(), clientId);
			// 重发publish消息存储服务 删除
			dupPublishMessageStoreService.removeByClient(clientId);
			LOGGER.debug("断开连接 删除重发publish消息存储 - clientIp: {}, clientId: {}",channel.remoteAddress().toString(), clientId);
			// 重发pubrel消息存储服务 删除
			dupPubRelMessageStoreService.removeByClient(clientId);
			LOGGER.debug("断开连接 删除重发pubrel消息存储 - clientIp: {}, clientId: {}",channel.remoteAddress().toString(), clientId);
		}
		// 会话存储服务 删除
		sessionStoreService.remove(clientId);
		LOGGER.debug("断开连接 删除会话存储服务 - clientIp: {}, clientId: {}",channel.remoteAddress().toString(), clientId);
		LOGGER.debug("DISCONNECT - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), clientId, sessionStore.isCleanSession());
		channel.close();
	}

}