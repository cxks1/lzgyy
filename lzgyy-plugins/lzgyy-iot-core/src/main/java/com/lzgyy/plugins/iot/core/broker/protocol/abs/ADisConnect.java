package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

/**
 * 断开连接处理抽象类
 */
public abstract class ADisConnect<T> {

	/**
	 * 会话存储服务接口
	 */
	protected ISessionStoreService sessionStoreService;
	
	/**
	 * 订阅存储服务接口
	 */
	protected ISubscribeStoreService subscribeStoreService;
	
	/**
	 * 重发publish消息存储服务接口
	 */
	protected IDupPublishMessageStoreService dupPublishMessageStoreService;
	
	/**
	 * 重发pubrel消息存储服务接口
	 */
	protected IDupPubRelMessageStoreService dupPubRelMessageStoreService;
	
	protected ADisConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
	}

	/**
	 * 处理断开连接
	 * @param channel
	 * @param msg
	 */
	protected abstract void processDisConnect(Channel channel, T msg);

}
