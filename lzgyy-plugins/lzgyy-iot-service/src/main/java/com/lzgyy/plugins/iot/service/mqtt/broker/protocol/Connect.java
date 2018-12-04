package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.auth.service.IAuthService;
import com.lzgyy.plugins.iot.core.broker.protocol.abs.AConnect;
import com.lzgyy.plugins.iot.core.store.message.bean.DupPubRelMessageStore;
import com.lzgyy.plugins.iot.core.store.message.bean.DupPublishMessageStore;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.bean.SessionStore;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

/**
 * 发起连接处理类
 */
public class Connect extends AConnect<MqttConnectMessage>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Connect.class);

	public Connect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService, IAuthService authService) {
		super(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
	}

	@Override
	public void processConnect(Channel channel, MqttConnectMessage msg) {
		
		// 判断传过来的报文是不是正确
		if (msg.decoderResult().isFailure()) {
			Throwable cause = msg.decoderResult().cause();
			if (cause instanceof MqttUnacceptableProtocolVersionException) {
				// 不支持的协议版本
				MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
					// 协议版本不被接受
					new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
				channel.writeAndFlush(connAckMessage);
				channel.close();
				return;
			} else if (cause instanceof MqttIdentifierRejectedException) {
				// 不合格的clientId
				MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
					// 连接拒绝
					new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
				channel.writeAndFlush(connAckMessage);
				channel.close();
				return;
			}
			channel.close();
			return;
		}
		
		// 判断clientId是否为空
		if (StrUtil.isBlank(msg.payload().clientIdentifier())) {
			MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				// 连接拒绝
				new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
			channel.writeAndFlush(connAckMessage);
			channel.close();
			return;
		}
		
		// 账号密码验证
		String username = msg.payload().userName();
		String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
		if (!authService.checkValid(username, password)) {
			MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				// 账号密码不正确
				new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
			channel.writeAndFlush(connAckMessage);
			channel.close();
			return;
		}
		
		// 如果终端设备的clean session的值为true，那么它离线之后，会话将销毁，相应的session进程也会销毁
		// 如果终端设备的clean session的值为false，那么它离线之后，会话将得以保留，相应的session进程也仍然存在
		//    也就是说，同一topic下，当设备A离线时，设备B在publish消息时，仍然可以匹配topic，进而找到这个session A进程，把消息发给设备A，缓存在消息队列里。设备A上线就可以收到离线消息。
		
		// 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
		if (sessionStoreService.containsKey(msg.payload().clientIdentifier())) {
			SessionStore sessionStore = sessionStoreService.get(msg.payload().clientIdentifier());
			Channel previous = sessionStore.getChannel();
			if (msg.variableHeader().isCleanSession()) {
				// 移除会话存储
				sessionStoreService.remove(msg.payload().clientIdentifier());
				LOGGER.debug("连接 【移除此终端会话存储】  - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
			}
			previous.close();
		}
		
		// 处理遗嘱信息
		SessionStore sessionStore = new SessionStore(msg.payload().clientIdentifier(), channel, msg.variableHeader().isCleanSession(), null);
		if (msg.variableHeader().isWillFlag()) {
			MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()), msg.variableHeader().isWillRetain(), 0),
					new MqttPublishVariableHeader(msg.payload().willTopic(), 0), Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes()));
			sessionStore.setWillMessage(willMessage);
		}
		// 处理连接心跳包
		if (msg.variableHeader().keepAliveTimeSeconds() > 0) {
			if (channel.pipeline().names().contains("idle")) {
				channel.pipeline().remove("idle");
			}
			channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, Math.round(msg.variableHeader().keepAliveTimeSeconds() * 1.5f)));
		}
		
		// 至此存储会话信息及返回接受客户端连接
		sessionStoreService.put(msg.payload().clientIdentifier(), sessionStore);
		// 将clientId存储到channel的map中
		channel.attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());
		Boolean sessionPresent = sessionStoreService.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
		MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				// 正常连接
				new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
		channel.writeAndFlush(okResp);
		
		// 如果cleanSession为0, 需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
		if (!msg.variableHeader().isCleanSession()) {
			// PUBLISH重发消息存储
			List<DupPublishMessageStore> dupPublishMessageStoreList = dupPublishMessageStoreService.get(msg.payload().clientIdentifier());
			// PUBREL重发消息存储
			List<DupPubRelMessageStore> dupPubRelMessageStoreList = dupPubRelMessageStoreService.get(msg.payload().clientIdentifier());
			dupPublishMessageStoreList.forEach(dupPublishMessageStore -> {
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getMqttQoS()), false, 0),
						new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()), Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes()));
				channel.writeAndFlush(publishMessage);
			});
			if (dupPublishMessageStoreList.size() > 0){
				LOGGER.debug("连接 【PUBLISH重发消息到此终端】  - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
			}
			dupPubRelMessageStoreList.forEach(dupPubRelMessageStore -> {
				MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
						MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()), null);
				channel.writeAndFlush(pubRelMessage);
			});
			if (dupPubRelMessageStoreList.size() > 0){
				LOGGER.debug("连接 【PUBREL重发消息到此终端】  - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
			}
		}
		
		// add by 2018-11-22 begin
		// 处理订阅存储，删除clientId的订阅
		if (msg.variableHeader().isCleanSession()) {
			subscribeStoreService.removeForClient(msg.payload().clientIdentifier());
			LOGGER.debug("连接 【移除此终端订阅存储】  - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
		}
		// end
		
		LOGGER.debug("CONNECT - clientIp: {}, clientId: {}, cleanSession: {}",channel.remoteAddress().toString(), msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
	}

}