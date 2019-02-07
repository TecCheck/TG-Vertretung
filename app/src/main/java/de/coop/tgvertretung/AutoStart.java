package de.coop.tgvertretung;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

    public static Thread dwdThread = null;

    public static void load() {



        if (dwdThread != null && dwdThread.isAlive()) {

        } else {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            Download.autoStart = true;
            dwdThread.start();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.prefs = context.getSharedPreferences("preferences", 0);
        Settings.load();
        load();
        Settings.save();
    }
}
