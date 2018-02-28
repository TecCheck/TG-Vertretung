package de.coop.tgvertretung;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AutoStart extends BroadcastReceiver {

    SharedPreferences settings = null;
    SharedPreferences table = null;
    public static final String PREFS_NAME = "Settings";
    public static final String TAB_NAME = "Table";

    @Override
    public void onReceive(Context context, Intent intent) {

        settings = context.getSharedPreferences(PREFS_NAME, 0);
        table = context.getSharedPreferences(TAB_NAME, 0);

        Client.saveOfflineBool = settings.getBoolean("saveOfflineBool", Client.saveOfflineBool);
        Client.filter = settings.getString("filter", Client.filter);
        //filter = settings.getInt("filter", filter);
        Client.extendet = settings.getBoolean("extendet", Client.extendet);
        Client.useFilter = settings.getBoolean("filterSwitch", Client.useFilter);

        load();
    }

    public void load() {
        //list is loading

        new Thread(new Download()).start();

            System.out.println("online: " + Download.online);

            if (Download.online) {

                Client.lastReloadStr = Client.CurrentTime(false);

                if(Client.saveOfflineBool) {
                    SharedPreferences.Editor tableEdit = table.edit();

                    tableEdit.putString("Time", Client.lastReloadStr);
                    tableEdit.putString("ServerTime", Client.lastserverRefreshStr);

                    tableEdit.apply();
                }
            } else {

                if(Client.saveOfflineBool){

                    Client.lastReloadStr = MainActivity.instance.table.getString("Time", MainActivity.instance.getString(R.string.never));
                    Client.lastserverRefreshStr = MainActivity.instance.table.getString("ServerTime", MainActivity.instance.getString(R.string.never));
                }
            }

        Download.online = true;


    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
