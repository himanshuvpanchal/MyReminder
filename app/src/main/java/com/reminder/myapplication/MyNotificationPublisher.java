package com.reminder.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class MyNotificationPublisher extends BroadcastReceiver {
    private static String default_notification_channel_id = "default";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_TITLE = "notification-title";
    public static String NOTIFICATION_DESC = "notification-desc";

    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String desc = intent.getStringExtra(NOTIFICATION_DESC);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(id), title, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id);
        builder.setContentTitle(title).
                setContentText(desc).
                setSmallIcon(R.drawable.icon).
                setDefaults(Notification.DEFAULT_SOUND).
                setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon512)).
                setAutoCancel(true);
        Intent appActivityIntent = new Intent(context, Edit.class);
        appActivityIntent.putExtra("ReminderId", id);
        PendingIntent contentAppActivityIntent = PendingIntent.getActivity(context, id, appActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentAppActivityIntent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId(String.valueOf(id));
        }
        assert notificationManager != null;
        notificationManager.notify(id, builder.build());
    }
}
