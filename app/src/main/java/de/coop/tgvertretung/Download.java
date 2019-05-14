package de.coop.tgvertretung;

import android.os.Handler;
import android.os.Looper;

import java.util.Date;

import de.sematre.tg.TG;

public class Download implements Runnable {

    public static boolean autoStart = false;
    public static int status = 0;

    @Override
    public void run() {

        //online
        status = 0;
        Client.print("AutoStart: " + autoStart);

        Client.print("Download started");
        try {
            Date date = Settings.settings.timeTable.getDate();
            TG tgv = new TG(Settings.settings.username, Settings.settings.password);
            Settings.settings.timeTable = tgv.getTimeTable().summarize();
            if(Settings.settings.timeTable.getDate().equals(date))
                //nothing new
                status = 2;
            Client.print("ServerTime: " + Settings.settings.timeTable);
            Client.print("Download finished");
        }catch (Exception e){
            e.printStackTrace();
            //offline
            status = 1;
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
