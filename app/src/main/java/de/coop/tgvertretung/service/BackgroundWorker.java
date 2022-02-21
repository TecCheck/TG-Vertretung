package de.coop.tgvertretung.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.storage.JsonStorageProvider;
import de.coop.tgvertretung.storage.StorageProvider;
import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.sematre.tg.TimeTable;

public class BackgroundWorker extends Worker implements Downloader.LoadFinishedListener {

    private static final String CHANNEL_ID = "TGV";
    private static final int NOTIFICATION_ID = 0;

    private final StorageProvider provider;
    private final SettingsWrapper settings;
    private final Downloader downloader;
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
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_icon_small);
        builder.setContentTitle(context.getString(R.string.title_notification));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setColorized(true);
        builder.setColor(context.getResources().getColor(R.color.colorAccent));
        builder.setContentIntent(getPendingIntent(context));
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
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
