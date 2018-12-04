package com.lzgyy.plugins.iot.core.broker.internal.service;

import com.lzgyy.plugins.iot.core.broker.internal.bean.InternalMessage;

/**
 * 内部通信, 基于发布-订阅范式
 */
public interface IInternalCommunication {
	
	public void internalListen();
	
	public void internalSend(InternalMessage internalMessage);

}