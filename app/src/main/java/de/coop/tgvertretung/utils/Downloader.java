package de.coop.tgvertretung.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import de.sematre.tg.TG;
import de.sematre.tg.TimeTable;

public class Downloader extends Thread {

    private static Downloader dwdThread = null;

    private int status = 0;
    private final LoadFinishedListener listener;
    private SettingsWrapper settings;

    public static boolean download(LoadFinishedListener listener, SettingsWrapper settings) {
        Utils.printMethod("download");

        if (dwdThread == null || !dwdThread.isAlive()) {
            try {
                dwdThread = new Downloader(listener, settings);
                dwdThread.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private Downloader(LoadFinishedListener listener, SettingsWrapper settings) {
        this.listener = listener;
        this.settings = settings;
    }

    @Override
    public void run() {
        // online
        status = 0;

        try {
            Utils.print("Download started");

            Log.d("Download", "usr: " + settings.getUsername() + ", pw: " + settings.getPassword());

            TG tgv = new TG(settings.getUsername(), settings.getPassword());
            TimeTable timeTable = tgv.getTimeTable().summarize().sort();

            if (Settings.settings.timeTable.getDate().equals(timeTable.getDate())) {
                // nothing new
                status = 2;
            }

            Settings.settings.timeTable = timeTable;

            Utils.print("ServerTime: " + Settings.settings.timeTable);
            Utils.print("Download finished");
        } catch (Exception e) {
            // offline
            e.printStackTrace();
            status = 1;
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            listener.loadFinished(status);
            Utils.print("Runnable started");
        });

        Utils.print("Download Thread closed");
        interrupt();
    }

    public interface LoadFinishedListener {
        void loadFinished(int status);
    }
}