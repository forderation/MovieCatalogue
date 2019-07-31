package com.example.moviecatalogue.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.moviecatalogue.BuildConfig;
import com.example.moviecatalogue.MainActivity;
import com.example.moviecatalogue.R;
import com.example.moviecatalogue.model.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import cz.msebera.android.httpclient.Header;

public class NotificationReminder extends BroadcastReceiver {
    public static final int ID_DAILY_REMINDER = 100;
    public static final int ID_RELEASE_TODAY = 200;
    public static final String TAG_TYPE_REMINDER = "tag_type_reminder";
    private final int MAX_NOTIF = 2;
    public static final String TAG_MESSAGE_NOTIFY = "tag_message_notify";
    public static final String TAG_TITLE_NOTIFY = "tag_title_notify";
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Calendar calendar;
    private ArrayList<Movie> movies = new ArrayList<>();
    public NotificationReminder(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int resultId = intent.getIntExtra(TAG_TYPE_REMINDER,0);
        if(resultId == ID_DAILY_REMINDER){
            showDailyNotify(context);
        }else if(resultId == ID_RELEASE_TODAY){
            calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(System.currentTimeMillis());
            String currentDate = DateFormat.format("yyyy-MM-dd",calendar).toString();
            getMovieUpcoming(context,currentDate);
        }
    }

    private void showReleaseTodayNotify(Context context, Intent intent,int idNotifyToday){
        String CHANNEL_ID = "Channel_200";
        String CHANNEL_NAME = "Release Today Reminder Channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent openDetail = new Intent(context,MainActivity.class);
        openDetail.putExtra(MainActivity.TAG_STATE_NAV_DRAWER,R.id.nav_released_now);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,openDetail,0);
        NotificationCompat.Builder builder;
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher_foreground);
        if(idNotifyToday<MAX_NOTIF){
            notificationManager.cancelAll();
             builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(intent.getStringExtra(TAG_TITLE_NOTIFY))
                    .setContentText(intent.getStringExtra(TAG_MESSAGE_NOTIFY))
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000,1000,1000,1000,1000})
                    .setSound(alarmSound)
                    .setAutoCancel(true)
                    .setGroup(context.getResources().getString(R.string.group_key_notify));
        }else{
            notificationManager.cancelAll();
            String header = context.getResources().getString(R.string.release_today_notify);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .addLine(header + movies.get(idNotifyToday).getOriginalTitle())
                    .addLine(header + movies.get(idNotifyToday - 1).getOriginalTitle())
                    .addLine(header + movies.get(idNotifyToday - 2).getOriginalTitle())
                    .setBigContentTitle(idNotifyToday + header)
                    .setSummaryText("Movie DB");
            builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setContentTitle(idNotifyToday + context.getResources().getString(R.string.new_released_movies))
                    .setContentText(context.getResources().getString(R.string.list_released_notify))
                    .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                    .setGroupSummary(true)
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = builder.build();
        notificationManager.notify(idNotifyToday,notification);
    }

    private void showDailyNotify(Context context){
        String CHANNEL_ID = "Channel_100";
        String CHANNEL_NAME = "Daily Reminder Channel";
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher_foreground))
                .setContentTitle(context.getResources().getString(R.string.daily_notify_title))
                .setContentText(context.getResources().getString(R.string.daily_notify_content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
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
            notificationManagerCompat.notify(ID_DAILY_REMINDER, notification);
        }
    }

    public boolean setDailyRemainder(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReminder.class);
        intent.putExtra(TAG_TYPE_REMINDER, ID_DAILY_REMINDER);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_DAILY_REMINDER,intent,0);
        if(alarmManager!=null){
            alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            return true;
        }
        return false;
    }

    public boolean setReleasedToday(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReminder.class);
        intent.putExtra(TAG_TYPE_REMINDER, ID_RELEASE_TODAY);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_RELEASE_TODAY,intent,0);
        if(alarmManager!=null){
            alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            return true;
        }
        return false;
    }

    public void getMovieUpcoming(final Context context, final String dateNow){
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/discover/movie?primary_release_date.gte=" + dateNow + "&api_key=" + BuildConfig.API_movie_DB +"&language=" + currentLocale;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject listObject = new JSONObject(response);
                    JSONArray resultList = listObject.getJSONArray("results");
                    movies.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        if(movieObject.getString("release_date").compareTo(dateNow) == 0){
                            Movie movie = new Movie(movieObject);
                            movies.add(movie);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int idNotifyToday = 0;
                Intent intent = new Intent();
                for(Movie movie:movies){
                    intent.putExtra(TAG_TITLE_NOTIFY, context.getResources().getString(R.string.today_release_notify) + movie.getOriginalTitle());
                    intent.putExtra(TAG_MESSAGE_NOTIFY, movie.getOverview());
                    showReleaseTodayNotify(context, intent, idNotifyToday);
                    idNotifyToday++;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "error data not found : " + statusCode, Toast.LENGTH_SHORT).show();
                client.cancelAllRequests(true);
            }
        });
    }
}
