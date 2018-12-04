package com.lzgyy.plugins.iot.core.store.subscribe.service;

import java.util.List;

import com.lzgyy.plugins.iot.core.store.subscribe.bean.SubscribeStore;

/**
 * 订阅存储服务接口
 */
public interface ISubscribeStoreService {

	/**
	 * 存储订阅
	 */
	void put(String topicFilter, SubscribeStore subscribeStore);

	/**
	 * 删除订阅
	 */
	void remove(String topicFilter, String clientId);

	/**
	 * 删除clientId的订阅
	 */
	void removeForClient(String clientId);
	
	/**
	 * 销毁
	 */
	void destroy();
	
	/**
	 * 获取订阅存储集
	 */
	List<SubscribeStore> search(String topic);
}