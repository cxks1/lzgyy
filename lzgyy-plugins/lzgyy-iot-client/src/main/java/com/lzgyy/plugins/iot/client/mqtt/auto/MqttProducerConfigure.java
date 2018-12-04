package com.lzgyy.plugins.iot.client.mqtt.auto;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.lzgyy.plugins.iot.client.mqtt.bootstrap.MqttProducer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.Producer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SubMessage;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Map;
import java.util.Optional;

/**
 * 自动配置类
 **/
@Configuration
@ConditionalOnClass({MqttProducer.class})
@EnableConfigurationProperties({ConnectOptions.class})
public class MqttProducerConfigure implements ApplicationContextAware,DisposableBean {

    private static final int _BLACKLOG =   1024;

    private static final int CPU =Runtime.getRuntime().availableProcessors();

    private static final int SEDU_DAY =10;

    private static final int TIMEOUT =120;

    private static final int BUF_SIZE=10*1024*1024;

    private ConfigurableApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean()
    public Producer initServer(ConnectOptions connectOptions, Environment env){
        MqttProducer mqttProducer = new MqttProducer();
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(MqttMessageListener.class);
        checkArgs(connectOptions);
        final SubMessage[] build = new SubMessage[1];
        Optional.of(beansWithAnnotation).ifPresent((Map<String, Object> mqttListener) -> {
            beansWithAnnotation.forEach((name, bean) -> {
                Class<?> clazz = AopUtils.getTargetClass(bean);
                if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
                    throw new IllegalStateException(clazz + " is not instance of " + MqttListener.class.getName());
                }
                MqttMessageListener annotation = clazz.getAnnotation(MqttMessageListener.class);
                MqttListener listener = (MqttListener) bean;
                mqttProducer.setMqttListener(listener);
                System.out.println("********* set the monitor successfully *********");
                build[0] = SubMessage.builder()
                        .qos(annotation.qos())
                        .topic(annotation.topic())
                        .build();
            });
        });
        // 连接
        mqttProducer.connect(connectOptions);
        // 订阅
        mqttProducer.sub(build[0]);
        return mqttProducer;
    }
    private void checkArgs(ConnectOptions connectOptions) {
        if(connectOptions.getServerIp()==null)
            throw new RuntimeException("ip地址为空");
        if(connectOptions.getServerPort()<1)
            throw new RuntimeException("端口号为空");
        if (connectOptions.getConnectTime()<1)
            connectOptions.setConnectTime(10);
        if (connectOptions.getConnectTimeOutMillis()<1)
            connectOptions.setConnectTimeOutMillis(30000);;
        if (connectOptions.getWriterIdleTimeSeconds()<1)
            connectOptions.setConnectTime(120);
        if(connectOptions.getMinPeriod()<1)
            connectOptions.setMinPeriod(10);
        if(connectOptions.getSoRevbuf()<1)
            connectOptions.setSoRevbuf(BUF_SIZE);
        if(connectOptions.getSoSndbuf()<1)
            connectOptions.setSoSndbuf(BUF_SIZE);
        ConnectOptions.MqttOpntions mqtt=connectOptions.getMqtt();
        if(mqtt!=null){
            if(mqtt.getClientIdentifier()==null)
                throw  new RuntimeException("设备号为空");
            if(mqtt.getKeepAliveTime()<1)
                mqtt.setKeepAliveTime(100);
            if (mqtt.getIsHasUserName()&&mqtt.getUserName()==null)
                throw new RuntimeException("未设置用户");
            if (mqtt.getIsHasPassword()&&mqtt.getPassword()==null)
                throw new RuntimeException("未设置密码");
            if(!mqtt.getIsWillFlag()){
                mqtt.setIsWillRetain(false);
                mqtt.setWillQos(0);
                mqtt.setWillMessage(null);
                mqtt.setWillTopic(null);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        Producer bean = applicationContext.getBean(Producer.class);
        if(bean!=null){
            bean.close();
        }
    }
}