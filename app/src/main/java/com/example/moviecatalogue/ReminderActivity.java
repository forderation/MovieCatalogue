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

import com.example.moviecatalogue.receiver.DailyReminder;
import com.example.moviecatalogue.receiver.ReleasedTodayReminder;

public class ReminderActivity extends AppCompatActivity {
    String keyReleasedToday, keyDailyReminder;
    private SharedPreferences sharedPref;
    private ReleasedTodayReminder releasedTodayReminder;
    private DailyReminder dailyReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Switch swDaily = findViewById(R.id.switch_daily);
        Switch swRelease = findViewById(R.id.switch_release);
        this.sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        keyDailyReminder = getString(R.string.key_daily_reminder);
        keyReleasedToday = getString(R.string.key_released_today_reminder);
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
        releasedTodayReminder = new ReleasedTodayReminder();
        dailyReminder = new DailyReminder();
    }

    private void startJobReleasedToday() {
        if (releasedTodayReminder.setReleasedToday(this)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(keyReleasedToday, true);
            editor.apply();
            showToast(getResources().getString(R.string.released_today_activated));
        } else {
            showToast(getResources().getString(R.string.released_today_activate_fail));
        }
    }

    private void cancelJobReleasedToday() {
        Intent intent = new Intent(this, ReleasedTodayReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ReleasedTodayReminder.ID_RELEASE_TODAY, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(keyReleasedToday, false);
        editor.apply();
        showToast(getResources().getString(R.string.daily_reminder_deactivated));
    }

    private void startJobDailyReminder() {
        if (dailyReminder.setDailyRemainder(this)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(keyDailyReminder, true);
            editor.apply();
            showToast(getResources().getString(R.string.daily_reminder_activated));
        } else {
            showToast(getResources().getString(R.string.fail_activated_daily_reminder));
        }
    }

    private void cancelJobDailyReminder() {
        Intent intent = new Intent(this, DailyReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), DailyReminder.ID_DAILY_REMINDER, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(keyDailyReminder, false);
        editor.apply();
        showToast(getResources().getString(R.string.released_today_deactivated));
    }

    private void showToast(String msgToast) {
        Toast.makeText(this, msgToast, Toast.LENGTH_SHORT).show();
    }
}
