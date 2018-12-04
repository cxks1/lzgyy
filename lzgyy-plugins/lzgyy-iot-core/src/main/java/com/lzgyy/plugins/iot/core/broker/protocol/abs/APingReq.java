package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

/**
 * PING请求连接处理
 */
public abstract class APingReq<T> {

	/**
	 * 处理PING请求
	 * @param channel
	 * @param msg
	 */
	protected abstract void processPingReq(Channel channel, T msg);

}
