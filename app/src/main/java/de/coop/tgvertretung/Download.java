package de.coop.tgvertretung;

import android.os.Handler;
import android.os.Looper;

import de.sematre.api.tg.TG;

public class Download implements Runnable {

    public static boolean online = true;

    @Override
    public void run() {
        Client.dwdThreadRunning = true;
        online = true;

        try {

            Client.tables.clear();

            System.out.println("Download started");

            TG tgv = new TG("226142", "tgrv");
            tgv.get();
            tgv.setFilterPrefix("");

            System.out.println("ServerTime: " + tgv.getTimeTable().getDate());
            Client.lastserverRefreshStr = tgv.getTimeTable().getDate();
            Client.tables = tgv.get();
            System.out.println("Download finished");

        } catch (Exception e) {
            e.printStackTrace();
            online = false;
        }
        Client.dwdThreadRunning = false;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Client.loadFinished();
                System.out.println("Runable started");
            }
        };

        mainHandler.post(myRunnable);
        System.out.println("Download Thread closed");

    }

}
