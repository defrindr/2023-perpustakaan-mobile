package com.example.perpusmini.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.widget.AlertDialogLayout;

import com.example.perpusmini.HalamanUtama;
import com.example.perpusmini.R;
import com.example.perpusmini.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        show(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());


    }

    @Override
    public void onNewToken(String s) {

        SharedPref.getInstance(this).storeToken(s);

    }

    private void show(String x, String y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("reminder", "reminder", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent i =new Intent(this, HalamanUtama.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,i,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"reminder").setContentTitle(x).setContentText(y).setSmallIcon(R.drawable.ic_stat_notification)
                .setAutoCancel(true).setContentIntent(pendingIntent).setPriority(NotificationManagerCompat.IMPORTANCE_MAX).setStyle(new NotificationCompat.BigTextStyle());

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(101,builder.build());


    }

}