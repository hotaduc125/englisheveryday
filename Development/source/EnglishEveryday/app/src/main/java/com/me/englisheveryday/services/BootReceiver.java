package com.me.englisheveryday.services;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.me.englisheveryday.provider.EnglishWidgetProvider;

/**
 * Created by enclaveit on 3/22/16.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, EnglishWidgetProvider.class));
        if (ids.length > 0) {
            Alarm.getInstance().setAlarm(context);
        }
    }
}
