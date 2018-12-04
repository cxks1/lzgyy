package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;

/**
 * 发布回执处理抽象类
 */
public abstract class APubAck<T> {

	/**
	 * 分布式生成报文标识符
	 */
	protected IMessageIdService messageIdService;
	
	/**
	 * PUBLISH重发消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制
	 */
	protected IDupPublishMessageStoreService dupPublishMessageStoreService;

	public APubAck(IMessageIdService messageIdService, IDupPublishMessageStoreService dupPublishMessageStoreService) {
		this.messageIdService = messageIdService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
	}
	
	/**
	 * 发布回执
	 * @param channel
	 * @param variableHeader
	 */
	protected abstract void processPubAck(Channel channel, T variableHeader);

}