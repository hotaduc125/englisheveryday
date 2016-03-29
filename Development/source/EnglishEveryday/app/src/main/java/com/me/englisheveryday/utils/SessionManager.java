package com.me.englisheveryday.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hotaduc on 3/15/16.
 */
public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "EEPref";
    private static final String INTERVAL = "interval";
    private static final String INTERVAL_TEXT = "interval_text";
    private static final String ENABLE_SPEAKER = "speaker";
    private static final String ENABLE_NOTIFICATION = "notification";
    private static final String INDEX = "index";
    private static final String START_ALARM = "start_alarm";
    private static final String APP_INSTALLED = "app_installed";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public boolean isStartAlarmFromBegining() {
        return pref.getBoolean(START_ALARM, false);
    }

    public void setAlarmStart(boolean alarm) {
        editor.putBoolean(START_ALARM, alarm);
        editor.commit();
    }

    public int getIndex() {
        return pref.getInt(INDEX, 0);
    }

    public void setIndex(int index) {
        editor.putInt(INDEX, index);
        editor.commit();
    }


    public String getIntervalText() {
        return pref.getString(INTERVAL_TEXT, "15 minutes");
    }

    public void setIntervalText(String strInterval) {
        editor.putString(INTERVAL_TEXT, strInterval);
        editor.commit();
    }

    public long getInterval() {
        return pref.getLong(INTERVAL, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    }

    public void setInterval(long interval) {
        editor.putLong(INTERVAL, interval);
        editor.commit();
    }

    public boolean isSpeakerEnabled() {
        return pref.getBoolean(ENABLE_SPEAKER, false);
    }

    public void setSpeakerEnable(boolean isEnabled) {
        editor.putBoolean(ENABLE_SPEAKER, isEnabled);
        editor.commit();
    }

    public boolean isNotified() {
        return pref.getBoolean(ENABLE_NOTIFICATION, false);
    }

    public void setNotification(boolean isEnabled) {
        editor.putBoolean(ENABLE_NOTIFICATION, isEnabled);
        editor.commit();
    }

    public boolean isAppInstalled() {
        return pref.getBoolean(APP_INSTALLED, false);
    }

    public void setAppInstalled(boolean isInstalled) {
        editor.putBoolean(APP_INSTALLED, isInstalled);
        editor.commit();
    }
}
