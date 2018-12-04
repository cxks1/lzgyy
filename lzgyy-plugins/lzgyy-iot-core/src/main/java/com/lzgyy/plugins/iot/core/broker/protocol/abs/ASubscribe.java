package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

/**
 * 订阅主题处理抽象类
 */
public abstract class ASubscribe<T> {

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

	public ASubscribe(ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService) {
		this.subscribeStoreService = subscribeStoreService;
		this.messageIdService = messageIdService;
		this.retainMessageStoreService = retainMessageStoreService;
	}
	
	/**
	 * 处理订阅主题
	 * @param channel
	 * @param msg
	 */
	protected abstract void processSubscribe(Channel channel, T msg);

}
