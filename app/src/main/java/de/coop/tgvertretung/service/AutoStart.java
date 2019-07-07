package de.coop.tgvertretung.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.coop.tgvertretung.utils.Settings;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.prefs = context.getSharedPreferences("preferences", 0);
        Settings.load(context);

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