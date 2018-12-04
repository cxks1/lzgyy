package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;

/**
 * QoS2消息完成处理抽象类
 */
public abstract class APubComp<T> {
	
	/**
	 * 分布式生成报文标识符接口
	 */
	protected IMessageIdService messageIdService;
	
	/**
	 * 重发pubrel消息存储服务接口, 当QoS=2时存在该重发机制
	 */
	protected IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	public APubComp(IMessageIdService messageIdService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		this.messageIdService = messageIdService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
	}
	
	/**
	 * QoS2消息完成处理
	 * @param channel
	 * @param variableHeader
	 */
	protected abstract void processPubComp(Channel channel, T variableHeader);
}
