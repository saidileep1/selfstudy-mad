package com.example.selfstudy_mad.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.selfstudy_mad.R;

public class NotificationHelper extends ContextWrapper {

    private static final String MY_CHANNEL_ID="com.example.selfstudy_mad";
    private static final String MY_CHANNEL_NAME="My App";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel mychannel=new NotificationChannel(MY_CHANNEL_ID,
                MY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        mychannel.enableLights(false);
        mychannel.enableVibration(true);
        mychannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(mychannel);

    }

    public NotificationManager getManager() {
        if (manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getChannnelNotification(String title, String body, PendingIntent contentIntent,
                                                        Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),MY_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
