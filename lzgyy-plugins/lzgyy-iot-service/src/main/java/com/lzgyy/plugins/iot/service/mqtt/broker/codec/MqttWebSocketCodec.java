package com.lzgyy.plugins.iot.service.mqtt.broker.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * WebSocket Mqtt消息编解码器
 */
public class MqttWebSocketCodec extends MessageToMessageCodec<BinaryWebSocketFrame, ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		out.add(new BinaryWebSocketFrame(msg.retain()));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		out.add(msg.retain().content());
	}
	
}