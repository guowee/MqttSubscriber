package com.missile.mqtt.subscriber;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MQTTService extends Service {

    public static final String BROKER_URL = "tcp://192.168.6.210:61613";
    public static String clientId = "android-client";

    public static String TOPIC = "test-topic";

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions options;

    private String username = "admin";
    private String password = "password";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clientId = DeviceUtil.getSerialNo();
        // host为主机名，clientId即连接MQTT的客户端ID，一般以唯一标识符表示
        mqttClient = new MqttAndroidClient(this, BROKER_URL, clientId);
        mqttClient.setCallback(new PushCallback(this));
        //MQTT连接设置
        options = new MqttConnectOptions();
        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置连接的用户名
        options.setUserName(username);
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
        options.setWill(TOPIC, "close".getBytes(), 2, true);
        connection();
        return super.onStartCommand(intent, flags, startId);
    }


    private void connection() {
        if (!mqttClient.isConnected() && isConnectNormal()) {
            try {
                mqttClient.connect(options, null, listener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private IMqttActionListener listener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            try {
                mqttClient.subscribe(TOPIC, 1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            exception.printStackTrace();
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
