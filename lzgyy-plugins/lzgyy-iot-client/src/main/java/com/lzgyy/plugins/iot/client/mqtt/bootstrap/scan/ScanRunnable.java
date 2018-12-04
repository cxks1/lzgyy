package com.lzgyy.plugins.iot.client.mqtt.bootstrap.scan;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.MqttApi;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SendMqttMessage;
import com.lzgyy.plugins.iot.client.mqtt.enums.ConfirmStatus;

/**
 * 扫描未确认信息
 **/
@Slf4j
public abstract class ScanRunnable  extends MqttApi implements Runnable {

    private ConcurrentLinkedQueue<SendMqttMessage> queue  = new ConcurrentLinkedQueue<>();

    public boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }

    @Override
    public void run() {
        if(!queue.isEmpty()) {
            SendMqttMessage poll;
            List<SendMqttMessage> list = new LinkedList<>();
            for (; (poll = queue.poll()) != null; ) {
                if (poll.getConfirmStatus() != ConfirmStatus.COMPLETE) {
                    list.add(poll);
                    doInfo(poll);
                }
                break;
            }
            addQueues(list);
        }
    }
    
    public abstract void doInfo( SendMqttMessage poll);

}