package com.me.englisheveryday.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hotaduc on 3/16/16.
 */
public class Utilities {

    private static final Utilities Instance = new Utilities();

    public static Utilities getInstance() {
        return Instance;
    }

    public String readJSONFromAsset(Context context) {
        String json;
        try {

            InputStream is = context.getAssets().open("dictionary.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
