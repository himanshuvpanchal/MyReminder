package com.reminder.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    Context context;
    AlarmManager alarmManager;

    public Common(Context _context, AlarmManager _alarmManager) {
        context = _context;
        alarmManager = _alarmManager;
    }

    void updateLabel(String Date, String Time, int ID, String Title, String Desc) {
        String FinalDate = Date + " " + Time;
        SimpleDateFormat datetime = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        java.util.Date date = null;
        try {
            date = datetime.parse(FinalDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            scheduleNotification(cal.getTimeInMillis(), ID, Title, Desc);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotification(long delay, int ID, String Title, String Desc) {
        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, ID);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_TITLE, Title);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_DESC, Desc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
            }
        }
    }


}
