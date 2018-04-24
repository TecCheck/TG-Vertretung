package de.coop.tgvertretung;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.sematre.api.tg.VertretungsTabelle;

public class AutoStart extends BroadcastReceiver {

    public static ArrayList<VertretungsTabelle> tables = new ArrayList<>();
    public static boolean saveOfflineBool = true;
    public static String lastReloadStr = "";
    public static String lastserverRefreshStr = "";
    public static String password = "";
    public static String username = "";
    public static Thread dwdThread = null;
    public static SharedPreferences table = null;
    public static Context con = null;


    public static void load() {

        if (dwdThread != null && dwdThread.isAlive()) {

        } else {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            Download.autoStart = true;
            dwdThread.start();

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
                    if(!tables.isEmpty()) {
                        SharedPreferences.Editor tableEdit = table.edit();
                        tableEdit.putString(con.getString(R.string.tab_time), lastReloadStr);
                        tableEdit.putString(con.getString(R.string.tab_servertime), lastserverRefreshStr);
                        tableEdit.putString(con.getString(R.string.tab_tables), ObjectSerializer.serialize(tables));

                        tableEdit.apply();
                    }
                }
            } else {

                if (saveOfflineBool) {
                    lastReloadStr = table.getString(con.getString(R.string.tab_time), con.getString(R.string.never));
                    lastserverRefreshStr = table.getString(con.getString(R.string.tab_servertime), con.getString(R.string.never));
                    tables = (ArrayList<VertretungsTabelle>) ObjectSerializer.deserialize(table.getString(con.getString(R.string.tab_tables), ObjectSerializer.serialize(tables)));

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
        con = context;
        table = context.getSharedPreferences(context.getString(R.string.tab_name), 0);
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.settings_name), 0);
        password = settings.getString(context.getString(R.string.settings_password), password);
        username = settings.getString(context.getString(R.string.settings_username), username);

        Client.print("prefs username: " + context.getString(R.string.settings_username) + ", prefs password: " + context.getString(R.string.settings_username));
        Client.print("username: " + AutoStart.username + ", password: " + AutoStart.password);
        load();
    }
}
