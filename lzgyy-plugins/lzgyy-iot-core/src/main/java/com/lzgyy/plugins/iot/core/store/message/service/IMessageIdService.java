package com.lzgyy.plugins.iot.core.store.message.service;

/**
 * 分布式生成报文标识符接口
 */
public interface IMessageIdService {

	/**
	 * 获取报文标识符
	 */
	int getNextMessageId();

	/**
	 * 释放报文标识符
	 */
	void releaseMessageId(int messageId);
}