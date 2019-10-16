package de.coop.tgvertretung.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Date;

import de.sematre.tg.TG;

public class Downloader extends Thread {

    private static Downloader dwdThread = null;

    private int status = 0;
    private LoadFinishedListener listener;

    public static boolean download(LoadFinishedListener listener) {
        Utils.printMethod("download");

        if (dwdThread == null || !dwdThread.isAlive()) {
            try {
                dwdThread = new Downloader(listener);
                dwdThread.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private Downloader(LoadFinishedListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        // online
        status = 0;

        try {
            Utils.print("Download started");

            Log.d("Download", "usr: " + Settings.settings.username + ", pw: " + Settings.settings.password);
            Date date = Settings.settings.timeTable.getDate();
            TG tgv = new TG(Settings.settings.username, Settings.settings.password);
            Settings.settings.timeTable = tgv.getTimeTable().summarize();
            if (Settings.settings.timeTable.getDate().equals(date)) {
                // nothing new
                status = 2;
            }

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