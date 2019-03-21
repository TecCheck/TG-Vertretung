package de.coop.tgvertretung;

import android.os.Handler;
import android.os.Looper;

import de.sematre.tg.TG;

public class Download implements Runnable {

    public static boolean online = true;
    public static boolean autoStart = false;

    @Override
    public void run() {

        online = true;
        Client.print("AutoStart: " + autoStart);

        Client.print("Download started");
        try {
            TG tgv = new TG(Settings.settings.username, Settings.settings.password);
            Settings.settings.timeTable = tgv.getTimeTable().summarize();
            Client.print("ServerTime: " + Settings.settings.timeTable);
            Client.print("Download finished");
        }catch (Exception e){
            e.printStackTrace();
            online = false;
        }
        if (!autoStart) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = () -> {
                Client.loadFinished();
                Client.print("Runnable started");
            };
            mainHandler.post(myRunnable);
        }else{
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = () -> {
                AutoStart.loadFinished();
                Client.print("Runnable started");
            };
            mainHandler.post(myRunnable);
        }
        Client.print("Download Thread closed");
    }
}
