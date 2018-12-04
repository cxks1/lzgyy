package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;

import com.lzgyy.plugins.iot.core.broker.internal.service.IInternalCommunication;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

/**
 * 发布消息处理抽象类
 */
public abstract class APublish<T> {
	
	/**
	 * 会话存储服务接口
	 */
	protected ISessionStoreService sessionStoreService;
	
	/**
	 * 订阅存储服务接口
	 */
	protected ISubscribeStoreService subscribeStoreService;
	
	/**
	 * 分布式生成报文标识符接口
	 */
	protected IMessageIdService messageIdService;
	
	/**
	 * 消息存储服务接口
	 */
	protected IRetainMessageStoreService retainMessageStoreService;
	
	/**
	 * 重发publish消息存储服务接口
	 */
	protected IDupPublishMessageStoreService dupPublishMessageStoreService;
	
	/**
	 * 内部通信, 基于发布-订阅范式
	 */
	protected IInternalCommunication iinternalCommunication;

	public APublish(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IInternalCommunication iinternalCommunication) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.messageIdService = messageIdService;
		this.retainMessageStoreService = retainMessageStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.iinternalCommunication = iinternalCommunication;
	}
	
	/**
	 * 处理发布消息
	 * @param channel
	 * @param msg
	 */
	protected abstract void processPublish(Channel channel, T msg);
	
}
