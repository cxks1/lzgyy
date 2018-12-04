package com.lzgyy.plugins.iot.client.mqtt.bootstrap.scan;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.Producer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SendMqttMessage;
import com.lzgyy.plugins.iot.client.mqtt.pool.Scheduled;

import io.netty.handler.codec.mqtt.MqttMessageType;

/**
 * 扫描消息确认
 **/
@Data
@Slf4j
public class SacnScheduled extends ScanRunnable {
	
	// 生产者接口定义, 发布/订阅消息接口
    private Producer producer;
    
    /**
     * ScheduledFuture只是在Future基础上还集成了Comparable和Delayed的接口。使其具有延迟、排序、获得异步计算结果的特性。 
		它用于表示ScheduledExecutorService中提交了任务的返回结果。我们通过Delayed的接口getDelay()方法知道该任务还有多久才会被执行。 
		JDK中并没提供ScheduledFuture的实现类。只有在ScheduledExecutorService中提交了任务，才能返回一个实现了ScheduledFuture接口的对象。
     */
    private ScheduledFuture<?> submit;
    // 秒
    private int seconds;

    public SacnScheduled(Producer producer,int seconds) {
        this.producer=producer;
        this.seconds=seconds;
    }

    public void start(){
        Scheduled  scheduled = new ScheduledPool();
        this.submit = scheduled.submit(this);
    }

    public void close(){
        if(submit!=null && !submit.isCancelled()){
            submit.cancel(true);
        }
    }
    
    /** 
     * 处理内容
     * SendMqttMessage 发送MQTT消息对象
     */
    @Override
    public void doInfo(SendMqttMessage poll) {
        if(producer.getChannel().isActive()){
            if(checkTime(poll)){
                poll.setTimestamp(System.currentTimeMillis());
                switch (poll.getConfirmStatus()){
                    case PUB:
                        poll.setDup(true);
                        pubMessage(producer.getChannel(),poll);
                        break;
                    case PUBREC:
                        sendAck(MqttMessageType.PUBREC,true,producer.getChannel(),poll.getMessageId());
                        break;
                    case PUBREL:
                        sendAck(MqttMessageType.PUBREL,true,producer.getChannel(),poll.getMessageId());
                        break;
                }

            }
        }
        else
        {
            log.info("channel is not alived");
            submit.cancel(true);
        }
    }
    // 检测是否超时
    private boolean checkTime(SendMqttMessage poll) {
        return System.currentTimeMillis()-poll.getTimestamp()>=seconds*1000;
    }

    private class ScheduledPool implements Scheduled {
        private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        public ScheduledFuture<?> submit(Runnable runnable){
            return scheduledExecutorService.scheduleAtFixedRate(runnable,2,2, TimeUnit.SECONDS);
        }
    }
}