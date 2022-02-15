package de.coop.tgvertretung.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.coop.tgvertretung.utils.SettingsWrapper;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsWrapper settings = new SettingsWrapper(context);

        if (settings.isLoggedIn() && !BackgroundService.isRunning) {
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}