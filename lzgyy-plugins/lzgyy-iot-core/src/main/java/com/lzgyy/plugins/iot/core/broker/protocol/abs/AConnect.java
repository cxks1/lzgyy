package com.lzgyy.plugins.iot.core.broker.protocol.abs;

import com.lzgyy.plugins.iot.core.auth.service.IAuthService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

import io.netty.channel.Channel;

/**
 * 发起连接处理抽象类
 */
public abstract class AConnect<T> {
	
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
	
	/**
	 * 用户和密码认证服务接口
	 */
	protected IAuthService authService;
	
	protected AConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService, IAuthService authService) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
		this.authService = authService;
	}
	
	/**
	 * 处理发起连接
	 * @param <T>
	 * @param channel
	 * @param msg
	 */
	protected void processConnect(Channel channel, T msg){};

}