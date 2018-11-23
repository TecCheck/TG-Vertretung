package de.coop.tgvertretung;

import android.os.Handler;
import android.os.Looper;

import de.sematre.api.tg.TG;

public class Download implements Runnable {

    public static boolean online = true;
    public static boolean autoStart = false;

    @Override
    public void run() {
        online = true;

        Client.print("autostart: " + autoStart);

        try {

            Client.print("Download started");
            TG tgv;
            //TG tgv = new TG("226142", "tgrv");

            if(!autoStart) {
                tgv = new TG(Client.username, Client.password);
                tgv.setSummarizeResults(true);
                tgv.setFilterPrefix("");
                Client.tables.clear();
                Client.tables = tgv.get();
                Client.lastserverRefreshStr = tgv.getTimeTable().getDate();
                Client.print("ServerTime: " + tgv.getTimeTable().getDate());
                Client.print("Download finished");
            }
            else {
                Client.print("username: " + AutoStart.username + ", password: " + AutoStart.password);
                tgv = new TG(AutoStart.username, AutoStart.password);
                tgv.setSummarizeResults(true);
                tgv.setFilterPrefix("");
                AutoStart.tables.clear();
                AutoStart.tables = tgv.get();
                AutoStart.lastserverRefreshStr = tgv.getTimeTable().getDate();
                Client.print("ServerTime: " + tgv.getTimeTable().getDate());
                Client.print("Download finished");
            }
        } catch (Exception e) {
            e.printStackTrace();
            online = false;
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable;
        if (autoStart) {
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    AutoStart.loadFinished();
                    Client.print("Runable AutoStarted");
                }
            };
        } else {
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    Client.loadFinished();
                    Client.print("Runable started");
                }
            };
        }

        mainHandler.post(myRunnable);
        Client.print("Download Thread closed");
    }
}
