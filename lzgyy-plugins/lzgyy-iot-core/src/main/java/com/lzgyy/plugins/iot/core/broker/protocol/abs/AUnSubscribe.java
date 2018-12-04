package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import io.netty.channel.Channel;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

/**
 * 取消订阅处理抽象类
 */
public abstract class AUnSubscribe<T> {
	
	/**
	 * 订阅存储服务接口
	 */
	protected ISubscribeStoreService subscribeStoreService;
	
	public AUnSubscribe(ISubscribeStoreService subscribeStoreService) {
		this.subscribeStoreService = subscribeStoreService;
	}
	
	/**
	 * 处理取消订阅
	 * @param channel
	 * @param msg
	 */
	protected abstract void processUnSubscribe(Channel channel, T msg);

}