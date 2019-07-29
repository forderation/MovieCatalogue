package com.example.moviecatalogue.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.moviecatalogue.MainActivity;
import com.example.moviecatalogue.R;

import java.util.Calendar;

public class NotificationRemainder extends BroadcastReceiver {
    public static final int ID_DAILY_REMAINDER = 100;
    public static final String TAG_TYPE_REMAINDER = "tag_type_remainder";
    public static final String TAG_MESSAGE_NOTIFY = "tag_message_notify";
    public static final String TAG_TITLE_NOTIFY = "tag_title_notify";
    public NotificationRemainder(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int resultId = intent.getIntExtra(TAG_TYPE_REMAINDER,0);
        String title = intent.getStringExtra(TAG_TITLE_NOTIFY);
        String message = intent.getStringExtra(TAG_MESSAGE_NOTIFY);
        if(resultId==ID_DAILY_REMAINDER){
            showDailyNotify(context,title,message);
        }
    }

    public void showDailyNotify(Context context, String title, String message){
        String CHANNEL_ID = "Channel_100";
        String CHANNEL_NAME = "Daily Remainder Channel";
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
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
            notificationManagerCompat.notify(ID_DAILY_REMAINDER, notification);
        }
    }

    public void setDailyRemainder(Context context, String message){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,NotificationRemainder.class);
        intent.putExtra(TAG_TYPE_REMAINDER,ID_DAILY_REMAINDER);
        intent.putExtra(TAG_MESSAGE_NOTIFY,message);
        intent.putExtra(TAG_TITLE_NOTIFY,"Daily Remainder Movie DB");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE,24);
        calendar.set(Calendar.SECOND,0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,ID_DAILY_REMAINDER,intent,0);
        if(alarmManager!=null){
            alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            Toast.makeText(context,"Daily Remainder has been activated",Toast.LENGTH_SHORT).show();
        }
    }
}
