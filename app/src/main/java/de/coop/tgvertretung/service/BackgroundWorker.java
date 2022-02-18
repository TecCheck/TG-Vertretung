package de.coop.tgvertretung.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;
import java.util.Random;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.storage.JsonStorageProvider;
import de.coop.tgvertretung.storage.StorageProvider;
import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.sematre.tg.TimeTable;

public class BackgroundWorker extends Worker implements Downloader.LoadFinishedListener {

    private static final String CHANNEL_ID = "TGV";

    private int notificationId = 0;
    private StorageProvider provider;
    private SettingsWrapper settings;
    private Downloader downloader;
    private Date currentNewest;

    public BackgroundWorker(Context context, WorkerParameters params) {
        super(context, params);
        provider = new JsonStorageProvider(context);
        settings = new SettingsWrapper(context);
        downloader = new Downloader(this);
        currentNewest = provider.readTimeTableSync().getDate();
    }

    @NonNull
    @Override
    public Result doWork() {
        downloader.download(currentNewest, settings.getUsername(), settings.getPassword(), null);
        return Result.success();
    }

    @Override
    public void loadFinished(Downloader.DownloadResult result, TimeTable timeTable) {
        if (result != Downloader.DownloadResult.SUCCESS)
            return;

        currentNewest = timeTable.getDate();
        provider.saveTimeTable(timeTable);
        makeNotification();
    }

    private void makeNotification() {
        Context context = getApplicationContext();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_icon_small);
        builder.setContentTitle(context.getString(R.string.title_notification));
        builder.setContentText(context.getString(R.string.new_content));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setColorized(true);
        builder.setColor(context.getResources().getColor(R.color.colorAccent));
        builder.setContentIntent(getPendingIntent(context));
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);

        notificationId = new Random().nextInt();
        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            flag = PendingIntent.FLAG_IMMUTABLE;

        return PendingIntent.getActivity(context, 0, intent, flag);
    }
}
