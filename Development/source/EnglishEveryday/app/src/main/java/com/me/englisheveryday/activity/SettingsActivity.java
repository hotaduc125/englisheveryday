package com.me.englisheveryday.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.me.englisheveryday.R;
import com.me.englisheveryday.services.Alarm;
import com.me.englisheveryday.utils.SessionManager;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Setting widget activity
 *
 * @author hotaduc
 * @version 1.0
 */
public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.cbSpeaker)
    CheckBox cbSpeaker;

    @Bind(R.id.cbNotification)
    CheckBox cbNotification;

    @Bind(R.id.tvInterval)
    TextView tvInterval;

    @Bind(R.id.tvVersion)
    TextView tvVersion;

    // Facebook package
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";

    // Twitter package
    public static final String TWITTER_PACKAGE = "com.twitter.android";

    /*
    init shared preferences
     */
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(this.getApplicationContext().getString(R.string.setting));
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        cbSpeaker.setChecked(sessionManager.isSpeakerEnabled());
        cbNotification.setChecked(sessionManager.isNotified());
        tvInterval.setText(sessionManager.getIntervalText());
        cbSpeaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sessionManager.setSpeakerEnable(isChecked);
                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, "Speaker icon will apply from new word's appearance", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sessionManager.setNotification(isChecked);
                if (isChecked) {
                    Alarm.getInstance().setAlarm(SettingsActivity.this);
                } else {
                    Alarm.getInstance().cancelAlarm(SettingsActivity.this);
                }
            }
        });
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvVersion.setText("English Flashcard Widget v."+ pInfo.versionName);
    }

    @OnClick(R.id.intervalLayout)
    public void setIntervalTime() {
        final CharSequence[] items = {"15 minutes", "1 hour", "12 hours", "24 hours"};
        final AlertDialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Please select the interval for refreshing new word");
        builder.setSingleChoiceItems(items, getIntervalSelectedPosition((int) sessionManager.getInterval()), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //TODO: Click on listview item
//                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String strInterval = ((AlertDialog) dialog).getListView().getItemAtPosition(position).toString();
                        tvInterval.setText(strInterval);
                        sessionManager.setIntervalText(strInterval);
                        if (getIntervalByMillisecond(position) != -1) {
                            sessionManager.setInterval(getIntervalByMillisecond(position));
                            Alarm.getInstance().setAlarm(SettingsActivity.this);
                        }
//                        Toast.makeText(SettingsActivity.this, ((AlertDialog) dialog).getListView().getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Toast.makeText(SettingsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        //TODO: Dismiss alert dialog
                    }
                });
        alert = builder.create();
        Log.i("ENGLISH_EVERYDAY", getIntervalSelectedPosition((int) sessionManager.getInterval()) + "");
        alert.getListView().setSelection(getIntervalSelectedPosition((int) sessionManager.getInterval()));
        alert.show();
    }

    /**
     * get interval in millisecond by position.
     *
     * @param position
     * @return
     */
    private long getIntervalByMillisecond(int position) {
        switch (position) {
            case 0:
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 1:
                return AlarmManager.INTERVAL_HOUR;
            case 2:
                return AlarmManager.INTERVAL_HALF_DAY;
            case 3:
                return AlarmManager.INTERVAL_DAY;
        }
        return -1;
    }

    /**
     * get position from selected interval
     *
     * @param value
     * @return
     */
    private int getIntervalSelectedPosition(int value) {
        switch (value) {
            case (int) AlarmManager.INTERVAL_FIFTEEN_MINUTES:
                return 0;
            case (int) AlarmManager.INTERVAL_HOUR:
                return 1;
            case (int) AlarmManager.INTERVAL_HALF_DAY:
                return 2;
            case (int) AlarmManager.INTERVAL_DAY:
                return 3;
        }
        return -1;
    }

    @OnClick(R.id.speakerLayout)
    public void enableSpeaker() {
        if (cbSpeaker.isChecked()) {
            cbSpeaker.setChecked(false);
        } else {
            cbSpeaker.setChecked(true);
        }
    }

    @OnClick(R.id.notificationLayout)
    public void enableNotification() {
        if (cbNotification.isChecked()) {
            cbNotification.setChecked(false);
        } else {
            cbNotification.setChecked(true);
        }
//        Toast.makeText(this, "" + cbNotification.isChecked(), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.rateLayout)
    public void rateApp(){

    }

    @OnClick(R.id.tellMyFriendLayout)
    public void tellMyFriends(){
        onShareClick(this);
    }


    /**
     * Sharing image to filtered social apps
     *
     * @param mActivity
     */
    public static void onShareClick(Activity mActivity) {
        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plaint");
        List<ResolveInfo> resInfos = mActivity.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                if (packageName.contains(TWITTER_PACKAGE) || packageName.contains(FACEBOOK_PACKAGE)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plaint");
                    intent.putExtra(Intent.EXTRA_TEXT, "my app url");
                    // Create the URI from the media
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                mActivity.startActivity(chooserIntent);
            } else {
                showWarningDialog(mActivity, "There is no social media installed");
            }
        }
    }

    /**
     * Display dialog to ask if user want to quit game.
     */
    public static void showWarningDialog(Context context, String msg) {
        new android.app.AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
