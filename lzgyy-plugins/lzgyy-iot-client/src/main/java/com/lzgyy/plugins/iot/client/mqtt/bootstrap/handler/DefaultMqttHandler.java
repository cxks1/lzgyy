package com.lzgyy.plugins.iot.client.mqtt.bootstrap.handler;

import com.lzgyy.plugins.iot.client.mqtt.auto.MqttListener;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.MqttProducer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.Producer;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;
import com.lzgyy.plugins.iot.client.mqtt.util.ByteBufUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认 mqtthandler处理
 **/
@ChannelHandler.Sharable
@Slf4j
public class DefaultMqttHandler extends MqttHander {

    private ClientMqttHandlerService mqttHandlerApi;

    private MqttProducer mqttProducer;

    private MqttListener mqttListener;
    
    private ConnectOptions connectOptions;

    public DefaultMqttHandler(ConnectOptions connectOptions, ClientMqttHandlerService mqttHandlerApi, Producer producer, MqttListener mqttListener) {
        super(mqttHandlerApi);
        this.connectOptions=connectOptions;
        this.mqttHandlerApi=mqttHandlerApi;
        this.mqttProducer =(MqttProducer) producer;
        this.mqttListener = mqttListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ConnectOptions.MqttOpntions mqtt = connectOptions.getMqtt();
        log.info("【DefaultMqttHandler：channelActive】"+ctx.channel().localAddress().toString()+"启动成功");
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT,false, MqttQoS.AT_LEAST_ONCE,false,10);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1_1.protocolName(),MqttVersion.MQTT_3_1_1.protocolLevel(),mqtt.getIsHasUserName(),mqtt.getIsHasPassword(),mqtt.getIsWillRetain(),mqtt.getWillQos(),mqtt.getIsWillFlag(),mqtt.getIsCleanSession(),mqtt.getKeepAliveTime());
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(mqtt.getClientIdentifier(), mqtt.getWillTopic(), mqtt.getWillMessage()==null?"":mqtt.getWillMessage(), mqtt.getUserName(), mqtt.getPassword());
        MqttConnectMessage mqttSubscribeMessage = new MqttConnectMessage(mqttFixedHeader,mqttConnectVariableHeader,mqttConnectPayload);
        channel.writeAndFlush(mqttSubscribeMessage);
    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()){
            case CONNACK:
            	// CONNACK	2	连接回执    （服务端到客户端，连接报文确认）
                mqttProducer.connectBack((MqttConnAckMessage) mqttMessage);
                break;
            case PUBLISH:
            	// PUBLISH	3	发布消息     （两个方向都允许）
                publish(channelHandlerContext.channel(),(MqttPublishMessage)mqttMessage);
                break;
            case PUBACK:
            	// PUBACK	4	发布回执     （两个方向都允许，QoS 1消息发布收到确认）
                mqttHandlerApi.puback(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREC:
            	// PUBREC	5	QoS2消息回执 （两个方向都允许，发布收到，保证交付第一步）
                mqttHandlerApi.pubrec(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREL:
            	// PUBREL	6	QoS2消息释放 （两个方向都允许，发布释放，保证交付第二步）
                mqttHandlerApi.pubrel(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBCOMP:
            	// PUBCOMP	7	QoS2消息完成 （两个方向都允许，消息发布完成，保证交互第三步）
                mqttHandlerApi.pubcomp(channelHandlerContext.channel(),mqttMessage);
                break;
            case SUBACK:
            	// SUBACK	9	订阅回执   （服务端到客户端，订阅请求报文确认）
                mqttHandlerApi.suback(channelHandlerContext.channel(),(MqttSubAckMessage)mqttMessage);
                break;
            case UNSUBACK:
            	// UNSUBACK	11	取消订阅回执 （服务端到客户端，取消订阅报文确认）
                mqttHandlerApi.unsubBack(channelHandlerContext.channel(),mqttMessage);
                break;
            default:
                break;
        }
    }

    private void publish(Channel channel,MqttPublishMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttMessage.variableHeader();
        ByteBuf payload = mqttMessage.payload();
        byte[] bytes = ByteBufUtil.copyByteBuf(payload); //
        if(mqttListener != null){
            mqttListener.callBack(mqttPublishVariableHeader.topicName(),new String(bytes));
            log.debug("PUBLISH MQTT监听器收到消息");
        }
        switch (mqttFixedHeader.qosLevel()){
            case AT_MOST_ONCE:
                break;
            case AT_LEAST_ONCE:
                mqttHandlerApi.pubBackMessage(channel,mqttPublishVariableHeader.messageId());
                log.debug("PUBLISH QoS 1消息发布收到确认");
                break;
            case EXACTLY_ONCE:
                mqttProducer.pubRecMessage(channel,mqttPublishVariableHeader.messageId());
                log.debug("PUBLISH 发布收到（保证交付第一步）");
                break;
        }
        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttProducer.getNettyBootstrapClient().doubleConnect();
        log.error("exception",cause);
        if(mqttListener!=null){
            mqttListener.callThrowable(cause);
        }
    }

}