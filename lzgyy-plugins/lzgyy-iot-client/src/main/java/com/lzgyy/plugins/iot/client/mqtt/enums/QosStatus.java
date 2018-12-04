package com.lzgyy.plugins.iot.client.mqtt.enums;

/**
 * Qos确认状态
 **/
public enum QosStatus {
    PUBD, // 已发送 没收到RECD （发送）
    RECD, //publish 推送回复过（发送）
}