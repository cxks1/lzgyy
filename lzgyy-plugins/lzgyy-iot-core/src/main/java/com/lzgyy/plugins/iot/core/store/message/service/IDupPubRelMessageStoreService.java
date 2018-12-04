package com.lzgyy.plugins.iot.core.store.message.service;

import java.util.List;

import com.lzgyy.plugins.iot.core.store.message.bean.DupPubRelMessageStore;

/**
 * 重发pubrel消息存储服务接口, 当QoS=2时存在该重发机制
 */
public interface IDupPubRelMessageStoreService {

	/**
	 * 存储消息
	 */
	void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore);

	/**
	 * 获取消息集合
	 */
	List<DupPubRelMessageStore> get(String clientId);

	/**
	 * 删除消息
	 */
	void remove(String clientId, int messageId);

	/**
	 * 删除消息
	 */
	void removeByClient(String clientId);
	
	/**
	 * 销毁
	 */
	void destroy();
}