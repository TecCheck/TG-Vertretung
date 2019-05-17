package de.coop.tgvertretung.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.Date;

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.Settings;
import de.sematre.tg.TG;

public class Download implements Runnable {

    public static boolean autoStart = false;
    public static int status = 0;

    @Override
    public void run() {
        // online
        status = 0;
        Client.print("AutoStart: " + autoStart);

        Client.print("Download started");
        try {
            Date date = Settings.settings.timeTable.getDate();
            TG tgv = new TG(Settings.settings.username, Settings.settings.password);
            Settings.settings.timeTable = tgv.getTimeTable().summarize();
            if (Settings.settings.timeTable.getDate().equals(date)) {
                // nothing new
                status = 2;
            }

            Client.print("ServerTime: " + Settings.settings.timeTable);
            Client.print("Download finished");
        } catch (Exception e) {
            // offline
            e.printStackTrace();
            status = 1;
        }

        if (!autoStart) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                Client.loadFinished();
                Client.print("Runnable started");
            });
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                AutoStart.loadFinished();
                Client.print("Runnable started");
            });
        }

        Client.print("Download Thread closed");
    }
}