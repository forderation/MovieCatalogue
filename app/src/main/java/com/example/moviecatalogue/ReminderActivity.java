package com.example.moviecatalogue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.moviecatalogue.receiver.NotificationRemainder;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sharedPref;
    private String keyDailyRemainder;
    private Switch swDaily, swRelease;
    private NotificationRemainder notificationRemainder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        swDaily = findViewById(R.id.switch_daily);
        swRelease = findViewById(R.id.switch_release);
        swDaily.setOnClickListener(this);
        swRelease.setOnClickListener(this);
        this.sharedPref = this.getSharedPreferences(getString(R.string.key_preference), Context.MODE_PRIVATE);
        keyDailyRemainder = getString(R.string.key_daily_remainder);
        if(sharedPref.getBoolean(keyDailyRemainder, false)){
            swDaily.setChecked(true);
        }else {
            swDaily.setChecked(false);
        }
        notificationRemainder = new NotificationRemainder();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor;
        switch (v.getId()){
            case R.id.switch_daily:
                editor = sharedPref.edit();
                if(swDaily.isChecked()){
                    cancelJob();
                    editor.putBoolean(keyDailyRemainder,false);
                    swDaily.setChecked(false);
                }else{
                    notificationRemainder.setDailyRemainder(this,"Check it we have new movies");
                    editor.putBoolean(keyDailyRemainder,true);
                    swDaily.setChecked(true);
                }
                editor.apply();
                break;
            case R.id.switch_release:
                showNotif();
                break;
        }
    }

    private void cancelJob(){
        Intent intent = new Intent(this,NotificationRemainder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),NotificationRemainder.ID_DAILY_REMAINDER,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        showToast("Daily remainder has been deactivated");
    }

    private void showToast(String msgToast){
        Toast.makeText(this,msgToast,Toast.LENGTH_SHORT).show();
    }

    private void showNotif(){
        String CHANNEL_ID = "Channel_100";
        String CHANNEL_NAME = "Daily Remainder Channel";
        Context context = getApplicationContext();
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Tes")
                .setContentText("Message")
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setChannelId(CHANNEL_ID);
            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(100, notification);
        }
    }
}
