package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;


/**
 * QoS2消息回执处理抽象类
 */
public abstract class APubRec<T> {
	
	/**
	 * 重发publish消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制
	 */
	protected IDupPublishMessageStoreService dupPublishMessageStoreService;
	
	/**
	 * 重发pubrel消息存储服务接口, 当QoS=2时存在该重发机制
	 */
	protected IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	protected APubRec(IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
	}
	
	/**
	 * QoS2消息回执
	 * @param channel
	 * @param variableHeader
	 */
	protected abstract void processPubRec(Channel channel, T variableHeader);

}