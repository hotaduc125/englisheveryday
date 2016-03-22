package com.me.englisheveryday.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.me.englisheveryday.R;
import com.me.englisheveryday.activity.SettingsActivity;
import com.me.englisheveryday.provider.EnglishWidgetProvider;
import com.me.englisheveryday.utils.SessionManager;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by hotaduc on 3/15/16.
 */
public class Alarm extends BroadcastReceiver {

    private static final Alarm Instance = new Alarm();

    public static Alarm getInstance(){
        return Instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    private static PendingIntent getAlarmIntent(Context context){
        Intent intent = new Intent(context, EnglishWidgetProvider.class);
        intent.setAction(EnglishWidgetProvider.UPDATE_BY_ALARM);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    public void setAlarm(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast = getAlarmIntent(context);
        alarmManager.cancel(broadcast);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 30000, broadcast);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, EnglishWidgetProvider.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
