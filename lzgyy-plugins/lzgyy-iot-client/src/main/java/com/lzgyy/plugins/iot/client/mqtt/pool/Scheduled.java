package com.lzgyy.plugins.iot.client.mqtt.pool;

import java.util.concurrent.ScheduledFuture;

/**
 * 接口
 **/
@FunctionalInterface
public interface Scheduled {
	
	/**
	 * ScheduledFuture只是在Future基础上还集成了Comparable和Delayed的接口。使其具有延迟、排序、获得异步计算结果的特性。 
		它用于表示ScheduledExecutorService中提交了任务的返回结果。我们通过Delayed的接口getDelay()方法知道该任务还有多久才会被执行。 
		JDK中并没提供ScheduledFuture的实现类。只有在ScheduledExecutorService中提交了任务，才能返回一个实现了ScheduledFuture接口的对象。
	 * @param runnable
	 * @return
	 */
    ScheduledFuture<?> submit(Runnable runnable);
}