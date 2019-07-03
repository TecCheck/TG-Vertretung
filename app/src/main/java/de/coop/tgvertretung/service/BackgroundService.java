package de.coop.tgvertretung.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Random;

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.R;
import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.utils.Download;

public class BackgroundService extends Service {

    public static final String CHANNEL_ID = "TGV";
    public static final boolean TEST = false;

    public static Runnable runnable = null;
    public static Runnable runnable2 = null;
    public static boolean isRunning;

    public Context context = this;
    public Handler handler = null;

    int notificationId = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isRunning = true;
        Log.d("BackgroundService", "Running");

        Settings.prefs = getSharedPreferences("preferences", 0);
        Settings.load();

        handler = new Handler();
        runnable = () -> {
            Client.load(false);
            if (!TEST)
                handler.postDelayed(runnable2, 10000);
            else
                handler.postDelayed(runnable2, 5000); //Test
        };

        runnable2 = () -> {
            if (Download.status == 0 || TEST) {
                Log.d("BackgroundService", "Download Status: " + Download.status);
                makeNotification();
            }
            if (!TEST)
                handler.postDelayed(runnable, 600000);
            else
                handler.postDelayed(runnable, 10000); //Test
        };

        handler.postDelayed(runnable, 5000);
    }

    private void makeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_icon_small);
        builder.setContentTitle(getString(R.string.title_notification));
        builder.setContentText(getString(R.string.new_content));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setColorized(true);
        builder.setColor(getResources().getColor(R.color.colorAccent));

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(notificationId);
        notificationId = (int) new Random().nextLong();
        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onDestroy() {
        //handler.removeCallbacks(runnable);
        //handler.removeCallbacks(runnable2);
    }
}