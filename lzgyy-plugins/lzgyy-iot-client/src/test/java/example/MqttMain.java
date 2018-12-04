package example;

import com.lzgyy.plugins.iot.client.mqtt.auto.MqttListener;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.MqttProducer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.Producer;
import com.lzgyy.plugins.iot.client.mqtt.bootstrap.bean.SubMessage;
import com.lzgyy.plugins.iot.client.mqtt.config.ConnectOptions;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * 测试
 **/
public class MqttMain {

    public static void main(String[] strings){
        Producer producer = new MqttProducer();
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setConnectTime(10);
        connectOptions.setServerIp("127.0.0.1");
        connectOptions.setServerPort(8883);
        connectOptions.setClientPort(8980);
        connectOptions.setIsSoKeepalive(true);
        connectOptions.setIsSoReuseaddr(true);
        connectOptions.setIsTcpNodelay(true);
        connectOptions.setSoRevbuf(1024);
        connectOptions.setSoSndbuf(1024);
        connectOptions.setConnectTimeOutMillis(30000);
        connectOptions.setWriterIdleTimeSeconds(60);
        connectOptions.setMinPeriod(10);
        connectOptions.setIsSsl(true);
        connectOptions.setJksFile("keystore/client.jks");
        connectOptions.setJksStorePass("112233445566");
        connectOptions.setJksKeyPass("1122334455668");
        
        ConnectOptions.MqttOpntions mqttOpntions = new ConnectOptions.MqttOpntions();
        mqttOpntions.setIsWillFlag(false);
        mqttOpntions.setClientIdentifier("lzgyy-iot-client-02");
        mqttOpntions.setWillTopic("/t1/t2");
        mqttOpntions.setWillMessage("");
        
        mqttOpntions.setIsHasUserName(true);
        mqttOpntions.setIsHasPassword(true);
        //mqttOpntions.setUserName("testUser");
        //mqttOpntions.setPassword("1939EE151640828386AF085E37430B8A9CF484AA59CA46CB3EEF5C6C92A7BBF893B4F75825C5E1109367E8D03902889A20ACBA0AF14A12C1EEF5712F5B30C396");
        mqttOpntions.setUserName("root");
        mqttOpntions.setPassword("7228788B19B1C983A10D20CB8E87749F0B7EFDD63D119B86693C85409FED1F5C345156305BD52B196962A01927EC0807553BDD05510EA1798AB7927CEF8BF63A");
        mqttOpntions.setIsWillRetain(true);
        mqttOpntions.setWillQos(0);
        mqttOpntions.setIsCleanSession(true);
        mqttOpntions.setKeepAliveTime(60);
        
        connectOptions.setMqtt(mqttOpntions);
        
        producer.setMqttListener(new MqttListener() {
            @Override
            public void callBack(String topic, String msg) {
                 System.out.println("========================================"+topic+msg);
            }
            @Override
            public void callThrowable(Throwable e) {
            	System.out.println(e);
            }
        });
        // 连接
        producer.connect(connectOptions);
        // 订阅
        producer.sub(SubMessage.builder().qos(MqttQoS.AT_LEAST_ONCE.valueOf(connectOptions.getMqtt().getWillQos()))
        		.topic(connectOptions.getMqtt().getWillTopic()).build());
        // 推送
        producer.pub("/t1/t2","你好呀，小宝贝",2);
    }

}