package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

/**
 * QoS2消息释放连接处理抽象类
 */
public abstract class APubRel<T> {
	
	/**
	 * 处理QoS2消息释放
	 * @param channel
	 * @param variableHeader
	 */
	protected abstract void processPubRel(Channel channel, T variableHeader);

}