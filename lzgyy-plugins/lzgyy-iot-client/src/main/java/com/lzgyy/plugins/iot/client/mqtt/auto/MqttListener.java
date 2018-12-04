package com.lzgyy.plugins.iot.client.mqtt.auto;

/**
 * call scan
 * mqtt监听，返回
 **/
public interface MqttListener{
	
	/**
	 * 返回 
	 * @param topic 主题
	 * @param msg   消息
	 */
    void callBack(String topic,String msg);
    
    /**
     * 返回错误
     * @param e
     */
    void callThrowable(Throwable e);
}