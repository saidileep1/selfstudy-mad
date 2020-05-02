package com.example.selfstudy_mad.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.selfstudy_mad.Helper.NotificationHelper;
import com.example.selfstudy_mad.OrderStatus;
import com.example.selfstudy_mad.R;
import com.example.selfstudy_mad.common.common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseInsatanceService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(remoteMessage);
        else
            showNotification(remoteMessage);
        
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        String title=notification.getTitle();
        String  content=notification.getBody();

        Intent intent=new Intent(this, OrderStatus.class);
        intent.putExtra(common.PHONE_TEXT, common.currentUser.getPhone());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper=new NotificationHelper(this);
        Notification.Builder builder=helper.getChannnelNotification(title,content,pendingIntent,defaultSoundUri);
//Generate random id for notification to show all notifactions
        helper.getManager().notify(new Random().nextInt(),builder.build());

    }

    private void showNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        Intent intent=new Intent(this, OrderStatus.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());

    }


}
