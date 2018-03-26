package de.coop.tgvertretung;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.sematre.api.tg.VertretungsTabelle;

public class AutoStart extends BroadcastReceiver {

    public static final String PREFS_NAME = "Settings";
    public static final String TAB_NAME = "Table";
    public static ArrayList<VertretungsTabelle> tables = new ArrayList<>();
    public static boolean saveOfflineBool = true;
    public static String lastReloadStr = "";
    public static String lastserverRefreshStr = "";
    public static Thread dwdThread = null;
    public static SharedPreferences settings = null;
    public static SharedPreferences table = null;

    public static void load() {

        if (dwdThread != null && dwdThread.isAlive()) {

        } else {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            dwdThread.start();
            Download.autoStart = true;
        }

    }

    public static void loadFinished() {

        Client.print("load Finished");
        //list is loading
        try {

            Client.print("online: " + Download.online);

            if (Download.online) {
                lastReloadStr = CurrentTime(false);

                if (saveOfflineBool) {
                    SharedPreferences.Editor tableEdit = table.edit();

                    tableEdit.putString("Time", lastReloadStr);
                    tableEdit.putString("ServerTime", lastserverRefreshStr);
                    tableEdit.putString("tables", ObjectSerializer.serialize(tables));

                    tableEdit.apply();
                }
            } else {

                if (saveOfflineBool) {
                    lastReloadStr = table.getString("Time", Resources.getSystem().getString(R.string.never));
                    lastserverRefreshStr = table.getString("ServerTime", MainActivity.instance.getString(R.string.never));
                    tables = (ArrayList<VertretungsTabelle>) ObjectSerializer.deserialize(table.getString("tables", ObjectSerializer.serialize(tables)));

                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Download.online = true;
    }

    public static String CurrentTime(boolean onlyDate) {
        Calendar calendar = new GregorianCalendar();
        String str = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR) + " ";
        if (!onlyDate) {
            if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                str += "0" + calendar.get(Calendar.HOUR_OF_DAY);
            } else {
                str += calendar.get(Calendar.HOUR_OF_DAY);
            }
            str += ":";

            if (calendar.get(Calendar.MINUTE) < 10) {
                str += "0" + calendar.get(Calendar.MINUTE);
            } else {
                str += calendar.get(Calendar.MINUTE);
            }
        }
        return str;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        table = context.getSharedPreferences(TAB_NAME, 0);
        load();
    }
}
