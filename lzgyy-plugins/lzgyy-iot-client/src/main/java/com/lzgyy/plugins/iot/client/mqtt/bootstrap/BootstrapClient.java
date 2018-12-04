package com.lzgyy.plugins.iot.client.mqtt.bootstrap;

import io.netty.channel.Channel;

/**
 * 启动类接口
 **/
public interface BootstrapClient {

	// 关闭
    void shutdown();
    
    // 初始化事件
    void initEventPool();
 
    // 开始
    Channel start();

}