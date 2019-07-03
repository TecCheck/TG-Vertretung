package de.coop.tgvertretung.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.service.BackgroundService;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.prefs = context.getSharedPreferences("preferences", 0);
        Settings.load();

        if (Settings.settings.loggedIn && !BackgroundService.isRunning) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}