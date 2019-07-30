package com.example.moviecatalogue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviecatalogue.receiver.NotificationReminder;

public class ReminderActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private NotificationReminder notificationReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Switch swDaily = findViewById(R.id.switch_daily);
        Switch swRelease = findViewById(R.id.switch_release);
        this.sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        String keyDailyReminder = getString(R.string.key_daily_reminder);
        String keyReleasedToday = getString(R.string.key_released_today_reminder);
        if (sharedPref.getBoolean(keyDailyReminder, false)) {
            swDaily.setChecked(true);
        } else {
            swDaily.setChecked(false);
        }
        if (sharedPref.getBoolean(keyReleasedToday, false)) {
            swRelease.setChecked(true);
        } else {
            swRelease.setChecked(false);
        }
        swDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startJobDailyReminder();
                } else {
                    cancelJobDailyReminder();
                }
            }
        });
        swRelease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startJobReleasedToday();
                } else {
                    cancelJobReleasedToday();
                }
            }
        });
        notificationReminder = new NotificationReminder();
    }

    private void startJobDailyReminder() {
        if (notificationReminder.setDailyRemainder(this)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.key_daily_reminder), true);
            editor.apply();
            showToast("Daily reminder has been activated");
        } else {
            showToast("Fail to activate daily reminder");
        }
    }

    private void startJobReleasedToday() {
        if (notificationReminder.setReleasedToday(this)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.key_released_today_reminder), true);
            editor.apply();
            showToast("Daily released today reminder has been activated");
        } else {
            showToast("Fail to activate daily released today reminder");
        }
    }

    private void cancelJobReleasedToday() {
        Intent intent = new Intent(this, NotificationReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NotificationReminder.ID_DAILY_REMINDER, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.key_daily_reminder), false);
        editor.apply();
        showToast("Daily reminder has been deactivated");
    }

    private void cancelJobDailyReminder() {
        Intent intent = new Intent(this, NotificationReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NotificationReminder.ID_RELEASE_TODAY, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.key_released_today_reminder), false);
        editor.apply();
        showToast("Released today reminder has been deactivated");
    }

    private void showToast(String msgToast) {
        Toast.makeText(this, msgToast, Toast.LENGTH_SHORT).show();
    }
}
