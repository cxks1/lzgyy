package com.lzgyy.plugins.iot.core.store.message.service;

import java.util.List;

import com.lzgyy.plugins.iot.core.store.message.bean.RetainMessageStore;

/**
 * 消息存储服务接口
 */
public interface IRetainMessageStoreService {

	/**
	 * 存储retain标志消息
	 */
	void put(String topic, RetainMessageStore retainMessageStore);

	/**
	 * 获取retain消息
	 */
	RetainMessageStore get(String topic);

	/**
	 * 删除retain标志消息
	 */
	void remove(String topic);

	/**
	 * 销毁
	 */
	void destroy();
	
	/**
	 * 判断指定topic的retain消息是否存在
	 */
	boolean containsKey(String topic);

	/**
	 * 获取retain消息集合
	 */
	List<RetainMessageStore> search(String topicFilter);

}