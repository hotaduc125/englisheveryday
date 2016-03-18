package com.me.englisheveryday.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;

import java.util.Locale;
import java.util.logging.LogRecord;

/**
 * Created by hotaduc on 3/15/16.
 */
public class SpeechService extends Service implements TextToSpeech.OnInitListener {

    public static final String WORD = "word";

    private TextToSpeech tts;
    private String strWord;
    private boolean isInit;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacksAndMessages(null);
        strWord = intent.getStringExtra(SpeechService.WORD);
        if (isInit) {
            speak();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 15 * 1000);
        return SpeechService.START_NOT_STICKY;
    }

    private void speak() {
        if (tts != null) {
            tts.speak(strWord, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak();
                isInit = true;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
