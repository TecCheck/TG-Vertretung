package de.coop.tgvertretung.service;

import android.app.ActivityManager;
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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import java.util.Random;
import java.util.Set;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.sematre.tg.TimeTable;

public class BackgroundService extends Service implements Downloader.LoadFinishedListener {

    private static final String CHANNEL_ID = "TGV";
    private static final boolean TEST = false;

    private Downloader downloader;
    private Runnable runnable;
    private Handler handler;
    private int notificationId = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Settings.load(this);
        SettingsWrapper settings = new SettingsWrapper(this);
        downloader = new Downloader(this);

        handler = new Handler();
        runnable = () -> {
            downloader.download(Settings.settings.timeTable.getDate(), settings.getUsername(), settings.getPassword());
            handler.postDelayed(runnable, !TEST ? 600000 : 10000);
        };

        handler.postDelayed(runnable, 5000);
    }

    private void makeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_icon_small);
        builder.setContentTitle(getString(R.string.title_notification));
        builder.setContentText(getString(R.string.new_content));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setColorized(true);
        builder.setColor(getResources().getColor(R.color.colorAccent));

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            flag = PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);
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

        notificationId = new Random().nextInt();
        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onDestroy() {
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void loadFinished(Downloader.DownloadResult result, TimeTable timeTable) {
        if (result == Downloader.DownloadResult.SUCCESS || TEST) {
            Log.d("BackgroundService", "DownloadResult: " + result);
            Settings.settings.timeTable = timeTable;
            Settings.save();
            makeNotification();
        }
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}