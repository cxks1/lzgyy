package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.internal.bean.InternalMessage;
import com.lzgyy.plugins.iot.core.broker.internal.service.IInternalCommunication;
import com.lzgyy.plugins.iot.core.broker.protocol.abs.APublish;
import com.lzgyy.plugins.iot.core.store.message.bean.DupPublishMessageStore;
import com.lzgyy.plugins.iot.core.store.message.bean.RetainMessageStore;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.bean.SubscribeStore;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import java.util.List;

/**
 * 发布消息
 */
public class Publish extends APublish<MqttPublishMessage>{

	private static final Logger LOGGER = LoggerFactory.getLogger(Publish.class);

	public Publish(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IInternalCommunication iinternalCommunication) {
		super(sessionStoreService, subscribeStoreService, messageIdService, retainMessageStoreService, dupPublishMessageStoreService, iinternalCommunication);
	}
	
	@Override
	public void processPublish(Channel channel, MqttPublishMessage msg) {
		// QoS=0
		// 至多一次，发完即丢弃，<=1
		if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
			byte[] messageBytes = new byte[msg.payload().readableBytes()];
			msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
			InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
				.setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
				.setDup(false).setRetain(false);
			iinternalCommunication.internalSend(internalMessage);
			this.sendPublishMessage(channel.localAddress().toString(), msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
		}
		// QoS=1
		// 至少一次，需要确认回复，>=1
		if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			byte[] messageBytes = new byte[msg.payload().readableBytes()];
			msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
			InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
				.setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
				.setDup(false).setRetain(false);
			iinternalCommunication.internalSend(internalMessage);
			this.sendPublishMessage(channel.localAddress().toString(), msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
			this.sendPubAckMessage(channel, msg.variableHeader().packetId());
		}
		// QoS=2
		// 只有一次，需要确认回复，＝1
		if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
			byte[] messageBytes = new byte[msg.payload().readableBytes()];
			msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
			InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
				.setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
				.setDup(false).setRetain(false);
			iinternalCommunication.internalSend(internalMessage);
			this.sendPublishMessage(channel.localAddress().toString(), msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
			this.sendPubRecMessage(channel, msg.variableHeader().packetId());
		}
		// retain=1, 保留消息
		// 待用，保留位置
		if (msg.fixedHeader().isRetain()) {
			byte[] messageBytes = new byte[msg.payload().readableBytes()];
			msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
			if (messageBytes.length == 0) {
				retainMessageStoreService.remove(msg.variableHeader().topicName());
			} else {
				RetainMessageStore retainMessageStore = new RetainMessageStore().setTopic(msg.variableHeader().topicName()).setMqttQoS(msg.fixedHeader().qosLevel().value())
					.setMessageBytes(messageBytes);
				retainMessageStoreService.put(msg.variableHeader().topicName(), retainMessageStore);
			}
		}
	}
	
	// 发布消息
	private void sendPublishMessage(String clientIp, String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
		List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
		subscribeStores.forEach(subscribeStore -> {
			if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
				// 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
				MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
				if (respQoS == MqttQoS.AT_MOST_ONCE) {
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - serviceIp: {}, clientId: {}, topic: {}, Qos: {}",clientIp, subscribeStore.getClientId(), topic, respQoS.value());
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.AT_LEAST_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - serviceIp: {}, clientId: {}, topic: {}, Qos: {}, messageId: {}",clientIp, subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
						.setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes);
					dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.EXACTLY_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - serviceIp: {}, clientId: {}, topic: {}, Qos: {}, messageId: {}",clientIp, subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
						.setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes);
					dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
			}
		});
	}
	
	// 发布回执
	private void sendPubAckMessage(Channel channel, int messageId) {
		MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(messageId), null);
		channel.writeAndFlush(pubAckMessage);
	}
	
	// QoS2消息回执
	private void sendPubRecMessage(Channel channel, int messageId) {
		MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(messageId), null);
		channel.writeAndFlush(pubRecMessage);
	}

}
