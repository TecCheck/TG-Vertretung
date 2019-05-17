package de.coop.tgvertretung.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.utils.Download;

public class AutoStart extends BroadcastReceiver {

    public static Thread dwdThread = null;

    public static void loadFinished() {
        Client.printMethod("loadFinished");

        if (Download.status == 0) {
            Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
        }
        Settings.save();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.prefs = context.getSharedPreferences("preferences", 0);
        Settings.load();
        dwdThread = new Thread(new Download(), "Download-Thread");
        Download.autoStart = true;
        dwdThread.start();
    }
}
