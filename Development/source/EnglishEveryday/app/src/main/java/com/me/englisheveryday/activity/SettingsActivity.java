package com.me.englisheveryday.activity;

import android.app.AlarmManager;
import android.content.DialogInterface;
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
        if (!sessionManager.isStartAlarmFromBegining()) {
            Alarm.getInstance().setAlarm(this);
        }

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

    @Override
    public void onBackPressed() {
        System.exit(1);
        super.onBackPressed();
    }
}
