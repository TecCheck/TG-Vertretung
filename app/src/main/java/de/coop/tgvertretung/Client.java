package de.coop.tgvertretung;

import android.content.SharedPreferences;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import de.sematre.api.tg.VertretungsTabelle;

public class Client {
    public static final String PREFS_NAME = "Settings";
    public static final String TAB_NAME = "Table";
    public static Client instance = null;
    public static ArrayList<VertretungsTabelle> tables = new ArrayList<>();
    public static String filter = "";
    //public static int filter = -1;
    public static boolean saveOfflineBool = true;
    public static boolean extendet = false;
    public static boolean useFilter = false;
    public static int currentView = 0;
    public static boolean viewUI = false;
    public static String lastReloadStr = "";
    public static String lastserverRefreshStr = "";
    public static Thread dwdThread = null;
    public int NothingRGB = 0x000000;
    public int VertretungRGB = 0xff000000;
    public int UberschriftRBG = 0xff3e31d3;
    public int NothingSize = 20;
    public int VertretungSize = 15;
    public int UberschriftSize = 25;

    public static boolean dwdThreadRunning = false;

    public Client() {
        if (instance == null) {
            instance = this;
        }
    }

    public static void load(boolean view) {
        viewUI = view;

        if (view) {
            MainActivity.instance.stdView.setVisibility(View.INVISIBLE);
            MainActivity.instance.loadView.setVisibility(View.VISIBLE);
            MainActivity.instance.progBar.setEnabled(true);
        }

        if(!dwdThreadRunning) {
            dwdThreadRunning = true;
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            dwdThread.start();
        }

    }

    public static void loadFinished() {

        System.out.println("load Finished");
        //list is loading
        try {

            System.out.println("online: " + Download.online);

            if (Download.online) {
                if (viewUI)
                    MainActivity.showSnack(MainActivity.instance.getString(R.string.connected));

                lastReloadStr = CurrentTime(false);

                if (saveOfflineBool) {
                    SharedPreferences.Editor tableEdit = MainActivity.instance.table.edit();

                    tableEdit.putString("Time", lastReloadStr);
                    tableEdit.putString("ServerTime", lastserverRefreshStr);
                    tableEdit.putString("tables", ObjectSerializer.serialize(tables));

                    tableEdit.apply();
                }
            } else {

                if (viewUI)
                    MainActivity.showSnack(MainActivity.instance.getString(R.string.noConnection));

                if (saveOfflineBool) {
                    lastReloadStr = MainActivity.instance.table.getString("Time", MainActivity.instance.getString(R.string.never));
                    lastserverRefreshStr = MainActivity.instance.table.getString("ServerTime", MainActivity.instance.getString(R.string.never));
                    tables = (ArrayList<VertretungsTabelle>) ObjectSerializer.deserialize(MainActivity.instance.table.getString("tables", ObjectSerializer.serialize(tables)));

                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (viewUI) {
            MainActivity.instance.startPagerView();
        }
        MainActivity.instance.setTable(getView(CurrentTime(true)));
        System.out.println(getView(CurrentTime(true)));

        Download.online = true;
        if (viewUI) {
            MainActivity.instance.loadView.setVisibility(View.GONE);
            MainActivity.instance.progBar.setEnabled(false);
            MainActivity.instance.stdView.setVisibility(View.VISIBLE);
        }
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

    public static int getView(String currentTime) {
        int i = 0;


        for (VertretungsTabelle vertretungsTabelle : tables) {

            if (vertretungsTabelle.getDate().contains(currentTime))
                return i;

            i++;
        }

        return 0;

    }

    public static int getDownloadThread(Set<Thread> threadSet) {

        int i = 0;

        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        System.out.println(threadSet.size());

        while (i < threadSet.size()) {
            if (threadArray[i].getName().equals("Download-Thread")) {
                return i;
            }
        }

        return -1;
    }

}
