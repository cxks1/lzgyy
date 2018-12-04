package com.lzgyy.plugins.iot.service.mqtt.broker.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.lzgyy.plugins.iot.core.store.session.bean.SessionStore;
import com.lzgyy.plugins.iot.service.mqtt.broker.protocol.ProtocolProcess;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * mqtt消息处理
 */
public class MqttBrokerHandler extends SimpleChannelInboundHandler<MqttMessage>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MqttBrokerHandler.class);
	
	/**
	 * 协议处理
	 */
	private ProtocolProcess protocolProcess;

	public MqttBrokerHandler(ProtocolProcess protocolProcess) {
		this.protocolProcess = protocolProcess;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
		if (null == msg.fixedHeader() || null == msg.fixedHeader().messageType()) {
			// 不知道发的啥，直接拒绝
			LOGGER.debug("不知道发的什么请求，直接拒绝 - clientIp: {}, clientId: {}",ctx.channel().remoteAddress().toString(),ctx, ctx.channel().attr(AttributeKey.valueOf("clientId")).get());
			MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
					// 连接拒绝
					new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
			ctx.writeAndFlush(connAckMessage);
			ctx.close();
			return;
		}
		
		switch (msg.fixedHeader().messageType()) {
			case CONNECT:
				// CONNECT	1	发起连接	（客户端请求连接服务端）
				protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
				break;
			case CONNACK:
				// CONNACK	2	连接回执    （服务端到客户端，连接报文确认）
				break;
			case PUBLISH:
				// PUBLISH	3	发布消息     （两个方向都允许）
				protocolProcess.publish().processPublish(ctx.channel(), (MqttPublishMessage) msg);
				break;
			case PUBACK:
				// PUBACK	4	发布回执     （两个方向都允许，QoS 1消息发布收到确认）
				protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREC:
				// PUBREC	5	QoS2消息回执 （两个方向都允许，发布收到，保证交付第一步）
				protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREL:
				// PUBREL	6	QoS2消息释放 （两个方向都允许，发布释放，保证交付第二步）
				protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBCOMP:
				// PUBCOMP	7	QoS2消息完成 （两个方向都允许，消息发布完成，保证交互第三步）
				protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case SUBSCRIBE:
				// SUBSCRIBE 8	订阅主题	（客户端到服务端，客户端订阅请求）
				protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
				break;
			case SUBACK:
				// SUBACK	9	订阅回执   （服务端到客户端，订阅请求报文确认）
				break;
			case UNSUBSCRIBE:
				// UNSUBSCRIBE 10 取消订阅 （客户端到服务端,客户端取消订阅请求）
				protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
				break;
			case UNSUBACK:
				// UNSUBACK	11	取消订阅回执 （服务端到客户端，取消订阅报文确认）
				break;
			case PINGREQ:
				// PINGREQ	12	PING请求      （客户端到服务端，心跳请求）
				protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
				break;
			case PINGRESP:
				// PINGRESP	13	PING响应      （服务端到客户端，心跳响应）
				break;
			case DISCONNECT:
				// DISCONNECT 14  断开连接	    （客户端到服务端，客户端断开连接）
				protocolProcess.disConnect().processDisConnect(ctx.channel(), msg);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 异常事件调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.debug("=============================== exceptionCaught 异常事件调用开始=====================================");
		System.out.println(cause);
		LOGGER.debug("=============================== exceptionCaught 异常事件调用结束=====================================");
		if (cause instanceof IOException) {
			// 远程主机强迫关闭了一个现有的连接的异常
			ctx.close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}
	
	/**
	 * 触发事件
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		LOGGER.debug("=============================== userEventTriggered 触发事件开始=====================================");
		// 超时的事件
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			Channel channel = ctx.channel();
			String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
			switch (idleStateEvent.state()) {
				case READER_IDLE:
					//  一段时间内没有数据接收
					LOGGER.debug("读超时 READER_IDLE - clientIp: {}, clientId: {}",channel.remoteAddress().toString(),ctx, clientId);
					// 发送遗嘱消息
					if (StrUtil.isNotBlank(clientId) && this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
						SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
						if (sessionStore.getWillMessage() != null) {
							this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
						}
					}
					ctx.close();
					break;
				case WRITER_IDLE:
					// 一段时间内没有数据发送
					LOGGER.debug("写超时 WRITER_IDLE - clientIp: {}, clientId: {}",channel.remoteAddress().toString(),ctx, clientId);
					break;
				case ALL_IDLE:
					// 一段时间内没有数据接收或者发送
					LOGGER.debug("总超时 ALL_IDLE - clientIp: {}, clientId: {}",channel.remoteAddress().toString(),ctx, clientId);
					// 发送遗嘱消息
					if (StrUtil.isNotBlank(clientId) && this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
						SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
						if (sessionStore.getWillMessage() != null) {
							this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
						}
					}
					ctx.close();
					break;
				default:
					break;
			}
			
		} else {
			super.userEventTriggered(ctx, evt);
		}
		LOGGER.debug("=============================== userEventTriggered 触发事件结束=====================================");
	}
}