package com.me.englisheveryday.provider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.me.englisheveryday.R;
import com.me.englisheveryday.activity.DictionaryActivity;
import com.me.englisheveryday.activity.SettingsActivity;
import com.me.englisheveryday.services.SpeechService;
import com.me.englisheveryday.utils.SessionManager;
import com.me.englisheveryday.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by hotaduc on 3/16/16.
 */
public class EnglishWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_BY_CLICKING = "update_clicking";
    public static final String SPEAKER = "speaker";
    public static final String UPDATE_BY_ALARM = "update_alarm";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) throws JSONException {
        ArrayList<String> content = getNewWord(context);
        String word = content.get(0);
        String sample = content.get(1);
        String type = content.get(2);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.english_widget);
        views.setTextViewText(R.id.tvWord, word);
        views.setTextViewText(R.id.tvSample, Html.fromHtml(sample));
        views.setTextViewText(R.id.tvWordType, type);
        views.setOnClickPendingIntent(R.id.btnNext, getPendingSeftIntent(context, UPDATE_BY_CLICKING));
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isSpeakerEnabled()) {
            views.setViewVisibility(R.id.btnSpeaker, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.btnSpeaker, getPendingSeftIntent(context, SPEAKER, word));
        } else {
            views.setViewVisibility(R.id.btnSpeaker, View.INVISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private ArrayList<String> getNewWord(Context context) throws JSONException {
        ArrayList<String> newWord = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(context);
        String content = Utilities.getInstance().readJSONFromAsset(context);
        JSONArray jsonArray = new JSONArray(content);
        newWord.add(0, jsonArray.getJSONObject(sessionManager.getIndex()).getJSONObject("newword").getString("word"));
        newWord.add(1, jsonArray.getJSONObject(sessionManager.getIndex()).getJSONObject("newword").getString("sample"));
        newWord.add(2, jsonArray.getJSONObject(sessionManager.getIndex()).getJSONObject("newword").getString("type"));
        newWord.add(3, jsonArray.getJSONObject(sessionManager.getIndex()).getJSONObject("newword").getString("notification"));
        return newWord;
    }

    private static PendingIntent getPendingSeftIntent(Context context, String action, String... content) {
        Intent intent = new Intent(context, EnglishWidgetProvider.class);
        intent.setAction(action);
        if (content != null && content.length == 1) {
            intent.putExtra(SpeechService.WORD, content[0]);
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void fireNotification(Context context, String title, String content, String ticker) {
        Intent notificationIntent = new Intent(context, DictionaryActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DictionaryActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(title)
                .setContentText(Html.fromHtml(content))
                .setTicker(ticker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content)))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.YELLOW, 3000, 3000)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager sessionManager = new SessionManager(context);
        switch (intent.getAction()) {
            case SPEAKER:
                Intent speakIntent = new Intent(context, SpeechService.class);
                speakIntent.putExtra(SpeechService.WORD, intent.getStringExtra(SpeechService.WORD));
                context.startService(speakIntent);
                break;
            case UPDATE_BY_CLICKING:
                if (sessionManager.getIndex() != 40) {
                    sessionManager.setIndex(sessionManager.getIndex() + 1);
                } else {
                    sessionManager.setIndex(0);
                }
                update(context);
                break;
            case UPDATE_BY_ALARM:
                if (sessionManager.getIndex() != 46) {
                    sessionManager.setIndex(sessionManager.getIndex() + 1);
                } else {
                    sessionManager.setIndex(0);
                }
                update(context);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sessionManager.isNotified()) {
                    ArrayList<String> content = null;
                    try {
                        content = getNewWord(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String word = content.get(0);
                    String sample = content.get(1);
                    String notification = content.get(3);
                    fireNotification(context, word, sample, notification);
                }
                break;
            default:
                super.onReceive(context, intent);
        }
    }
}
