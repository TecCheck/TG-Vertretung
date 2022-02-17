package de.coop.tgvertretung.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Date;

import de.sematre.tg.TG;
import de.sematre.tg.TimeTable;

public class Downloader {

    private final LoadFinishedListener listener;

    private Thread downloadThread = null;

    public Downloader(LoadFinishedListener listener) {
        this.listener = listener;
    }

    public boolean download(Date currentNewestDate, String username, String password) {
        if (downloadThread != null && downloadThread.isAlive())
            return false;

        downloadThread = new Thread("downloader") {
            @Override
            public void run() {
                DownloadResult result = DownloadResult.FAILED;
                TimeTable timeTable = null;

                try {
                    TG tgv = new TG(username, password);
                    timeTable = tgv.getTimeTable().summarize().sort();
                    result = DownloadResult.SUCCESS;

                    if (currentNewestDate.equals(timeTable.getDate()))
                        result = DownloadResult.NOTHING_NEW;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final DownloadResult finalResult = result;
                final TimeTable finalTimeTable = timeTable;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> listener.loadFinished(finalResult, finalTimeTable));
            }
        };

        downloadThread.start();
        return true;
    }

    public interface LoadFinishedListener {
        void loadFinished(DownloadResult result, TimeTable timeTable);
    }

    public enum DownloadResult {
        SUCCESS,
        NOTHING_NEW,
        FAILED
    }
}