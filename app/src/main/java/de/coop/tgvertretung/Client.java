package de.coop.tgvertretung;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.sematre.tg.Table;

public class Client {
    private static final boolean SYSOUT = true;
    private static final boolean METHOD_SYSOUT = false;
    public static Client instance = null;
    private static boolean viewUI = false;
    private static boolean firstPagerStart = true;
    private static Thread dwdThread = null;
    public int VertretungRGB = 0xff000000;
    public int NothingSize = 20;
    public int VertretungSize = 15;

    public Client() {
        if (instance == null) {
            instance = this;
        }
    }

    public static void load(boolean view) {
        Client.printMethod("loadFinished");
        viewUI = view;

        if (view) {
            MainActivity.instance.stdView.setVisibility(View.INVISIBLE);
            MainActivity.instance.loadView.setVisibility(View.VISIBLE);
            MainActivity.instance.progBar.setEnabled(true);
        }

        if (dwdThread == null || !dwdThread.isAlive()) {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            dwdThread.start();
        }
    }

    public static void loadFinished() {
        Client.printMethod("loadFinished");

        Client.print("viewUI: " + viewUI);
        //list is loading
        Client.print("online: " + Download.online);

        if (Download.online && viewUI) {
            MainActivity.showSnack(MainActivity.instance.getString(R.string.connected));

        } else if (viewUI) {
            MainActivity.showSnack(MainActivity.instance.getString(R.string.noConnection));
        }
        if (Download.online) {
            Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
        }
        if (viewUI) {
            int i = MainActivity.instance.mPager.getCurrentItem();
            MainActivity.instance.startPagerView();
            Client.print("Pager started!");
            if (firstPagerStart) {
                MainActivity.instance.setTable(getView());
                firstPagerStart = false;
            } else
                MainActivity.instance.setTable(i);
        }

        if (viewUI) {
            MainActivity.instance.loadView.setVisibility(View.GONE);
            MainActivity.instance.progBar.setEnabled(false);
            MainActivity.instance.stdView.setVisibility(View.VISIBLE);
            Client.print("Pager visible");
        }
    }

    public static String getFormattedDate(Date date, boolean dayName, boolean useTime) {
        String pattern = "dd.MM.yyyy";
        if (useTime) pattern += " HH:mm";
        if(dayName) pattern = "EEEE " + pattern;

        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static int getView() {
        Client.printMethod("getView");
        int i = 0;

        for (Table table : Settings.settings.timeTable.getTables()) {
            Date today = new Date(System.currentTimeMillis());
            Date tableDate = table.getDate();
            if (today.getDay() == tableDate.getDay() && today.getMonth() == tableDate.getMonth() && today.getYear() == tableDate.getYear()) {
                return i;
            }
            Client.print(table.getDate().toString());
            i++;
        }
        Client.print("Date not found");
        return 0;

    }

    public static void print(String text) {
        if (SYSOUT)
            System.out.println(text);
    }

    public static void printMethod(String name) {
        if (METHOD_SYSOUT) {
            System.out.println(name + "();");
        }
    }
}