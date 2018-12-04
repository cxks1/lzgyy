package com.lzgyy.plugins.iot.service.mqtt.broker.protocol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzgyy.plugins.iot.core.auth.service.IAuthService;
import com.lzgyy.plugins.iot.service.mqtt.broker.internal.InternalCommunication;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPubRelMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IDupPublishMessageStoreService;
import com.lzgyy.plugins.iot.core.store.message.service.IMessageIdService;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;
import com.lzgyy.plugins.iot.core.store.session.service.ISessionStoreService;
import com.lzgyy.plugins.iot.core.store.subscribe.service.ISubscribeStoreService;

/**
 * 协议处理
 */
@Component
public class ProtocolProcess {
	
	/**
	 * 会话存储服务接口
	 */
	@Autowired
	private ISessionStoreService sessionStoreService;
	
	/**
	 * 订阅存储服务接口
	 */
	@Autowired
	private ISubscribeStoreService subscribeStoreService;
	
	/**
	 * 重发publish消息存储服务接口
	 */
	@Autowired
	private IDupPublishMessageStoreService dupPublishMessageStoreService;
	
	/**
	 * 重发pubrel消息存储服务接口
	 */
	@Autowired
	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;
	
	/**
	 * 用户和密码认证服务接口
	 */
	@Autowired
	private IAuthService authService;
	
	/**
	 * 分布式生成报文标识符
	 */
	@Autowired
	private IMessageIdService messageIdService;
	
	/**
	 * 消息存储服务接口
	 */
	@Autowired
	private IRetainMessageStoreService messageStoreService;
	
	/**
	 * 内部通信, 基于发布-订阅范式
	 */
	@Autowired
	private InternalCommunication internalCommunication;
	
	/**
	 * 发起连接处理
	 */
	private Connect connect;
	
	/**
	 * 订阅主题处理
	 */
	private Subscribe subscribe;
	
	/**
	 * 取消订阅处理
	 */
	private UnSubscribe unSubscribe;
	
	/**
	 * 发布消息处理
	 */
	private Publish publish;
	
	/**
	 * 断开连接
	 */
	private DisConnect disConnect;
	
	/**
	 * PING请求连接处理
	 */
	private PingReq pingReq;
	
	/**
	 * QoS2消息释放
	 */
	private PubRel pubRel;
	
	/**
	 * 发布回执
	 */
	private PubAck pubAck;
	
	/**
	 * QoS2消息回执
	 */
	private PubRec pubRec;
	
	/**
	 * QoS2消息完成
	 */
	private PubComp pubComp;
	
	// 发起连接
	public Connect connect() {
		if (connect == null) {
			connect = new Connect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
		}
		return connect;
	}
	
	// 订阅主题
	public Subscribe subscribe() {
		if (subscribe == null) {
			subscribe = new Subscribe(subscribeStoreService, messageIdService, messageStoreService);
		}
		return subscribe;
	}
	
	// 发布消息
	public Publish publish() {
		if (publish == null) {
			publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, messageStoreService, dupPublishMessageStoreService, internalCommunication);
		}
		return publish;
	}
	
	// 断开订阅
	public UnSubscribe unSubscribe() {
		if (unSubscribe == null) {
			unSubscribe = new UnSubscribe(subscribeStoreService);
		}
		return unSubscribe;
	}
	
	// 断开连接
	public DisConnect disConnect() {
		if (disConnect == null) {
			disConnect = new DisConnect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService);
		}
		return disConnect;
	}
	
	// PING请求
	public PingReq pingReq() {
		if (pingReq == null) {
			pingReq = new PingReq();
		}
		return pingReq;
	}
	
	// QoS2消息释放
	public PubRel pubRel() {
		if (pubRel == null) {
			pubRel = new PubRel();
		}
		return pubRel;
	}
	
	// 发布回执
	public PubAck pubAck() {
		if (pubAck == null) {
			pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
		}
		return pubAck;
	}
	
	// QoS2消息回执
	public PubRec pubRec() {
		if (pubRec == null) {
			pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
		}
		return pubRec;
	}
	
	// QoS2消息完成
	public PubComp pubComp() {
		if (pubComp == null) {
			pubComp = new PubComp(messageIdService, dupPubRelMessageStoreService);
		}
		return pubComp;
	}
	
	// 获得会话存储服务接口
	public ISessionStoreService getSessionStoreService() {
		return sessionStoreService;
	}
}