package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzgyy.plugins.iot.core.broker.protocol.abs.ASubscribe;
import com.lzgyy.plugins.iot.core.store.message.bean.RetainMessageStore;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.bean.SubscribeStore;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅主题
 */
public class Subscribe extends ASubscribe<MqttSubscribeMessage>{

	private static final Logger LOGGER = LoggerFactory.getLogger(Subscribe.class);

	public Subscribe(ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService) {
		super(subscribeStoreService, messageIdService, retainMessageStoreService);
	}
	
	@Override
	public void processSubscribe(Channel channel, MqttSubscribeMessage msg) {
		List<MqttTopicSubscription> topicSubscriptions = msg.payload().topicSubscriptions();
		if (this.validTopicFilter(topicSubscriptions)) {
			String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
			List<Integer> mqttQoSList = new ArrayList<Integer>();
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				SubscribeStore subscribeStore = new SubscribeStore(clientId, topicFilter, mqttQoS.value());
				subscribeStoreService.put(topicFilter, subscribeStore);
				mqttQoSList.add(mqttQoS.value());
				LOGGER.debug("SUBSCRIBE - clientIp: {}, clientId: {}, topFilter: {}, QoS: {}",channel.remoteAddress().toString(), clientId, topicFilter, mqttQoS.value());
			});
			MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
				new MqttSubAckPayload(mqttQoSList));
			channel.writeAndFlush(subAckMessage);
			LOGGER.debug("SUBSCRIBE -SUBACK 订阅回执 clientIp: {}, clientId: {}",channel.remoteAddress().toString(), clientId);
			// 发布保留消息
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				this.sendRetainMessage(channel, topicFilter, mqttQoS);
			});
		} else {
			channel.close();
		}
	}
	
	// 验证主题过滤
	private boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {
		for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
			String topicFilter = topicSubscription.topicName();
			// 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理, 这里没有参考标准协议
			if (StrUtil.startWith(topicFilter, '#') || StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/') || !StrUtil.contains(topicFilter, '/')) return false;
			if (StrUtil.contains(topicFilter, '#')) {
				// 不是以/#字符串结尾的订阅按非法订阅处理
				if (!StrUtil.endWith(topicFilter, "/#")) return false;
				// 如果出现多个#符号的订阅按非法订阅处理
				if (StrUtil.count(topicFilter, '#') > 1) return false;
			}
			if (StrUtil.contains(topicFilter, '+')) {
				//如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
				if (StrUtil.count(topicFilter, '+') != StrUtil.count(topicFilter, "/+")) return false;
			}
		}
		return true;
	}
	
	// 保留消息
	private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS mqttQoS) {
		List<RetainMessageStore> retainMessageStores = retainMessageStoreService.search(topicFilter);
		retainMessageStores.forEach(retainMessageStore -> {
			MqttQoS respQoS = retainMessageStore.getMqttQoS() > mqttQoS.value() ? mqttQoS : MqttQoS.valueOf(retainMessageStore.getMqttQoS());
			if (respQoS == MqttQoS.AT_MOST_ONCE) {
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), 0), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientIp: {}, clientId: {}, topic: {}, Qos: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value());
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.AT_LEAST_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientIp: {}, clientId: {}, topic: {}, Qos: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.EXACTLY_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientIp: {}, clientId: {}, topic: {}, Qos: {}, messageId: {}",channel.remoteAddress().toString(), (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
		});
	}

}