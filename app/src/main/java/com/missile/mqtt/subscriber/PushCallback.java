package com.missile.mqtt.subscriber;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class PushCallback implements MqttCallback {

    private ContextWrapper context;

    public PushCallback(ContextWrapper context) {
        this.context = context;
    }


    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //有新消息到达时的回调方法
        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notification;
        final Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("Message")
                .setContentText(new String(message.getPayload()) + " ")
                .setContentIntent(activity)
                .setSmallIcon(R.mipmap.snow)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        notification = builder.getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.number += 1;
        notificationManager.notify(0, notification);

        Intent it = new Intent(context, DownloadService.class);
        String url = new String(message.getPayload());
        if (url.endsWith(".apk")) {
            it.putExtra("url", url);
            context.startService(it);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
