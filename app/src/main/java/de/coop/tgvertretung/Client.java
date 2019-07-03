package de.coop.tgvertretung;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.utils.Download;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;

public class Client {

    private static final boolean SYSOUT = true;
    private static final boolean METHOD_SYSOUT = false;

    public static Client instance = null;

    private static boolean viewUI = false;
    private static boolean firstPagerStart = true;
    private static Thread dwdThread = null;

    public int vertretungRGB = 0xff000000;
    public int nothingSize = 20;
    public int vertretungSize = 15;

    public Client() {
        if (instance == null) instance = this;
    }

    public static void load(boolean view) {
        Client.printMethod("load");

        if (dwdThread == null || !dwdThread.isAlive()) {
            dwdThread = new Thread(new Download(), "Download-Thread");
            dwdThread.start();

            viewUI = view;
            if (view) {
                if (Settings.settings.useOldLayout) {
                    Utils.mainActivity.stdView.setVisibility(View.INVISIBLE);
                    Utils.mainActivity.loadView.setVisibility(View.VISIBLE);
                    Utils.mainActivity.progBar.setEnabled(true);
                } else {
                    Client.print("SwipeRefreshLayout");
                    Utils.mainActivity.refreshLayout.setRefreshing(true);
                }
            }
        }
    }

    public static void loadFinished() {
        Client.printMethod("loadFinished");

        if (Download.status == 0 || Download.status == 2) {
            Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
            Settings.save();
        }

        if (viewUI) {

            Utils.mainActivity.refreshLayout.setRefreshing(false);
            Utils.mainActivity.refreshLayout.setColorSchemeColors(Utils.getColor(Settings.settings.timeTable.getTables().get(0).getDate()));

            if (Download.status == 0 && viewUI) {
                MainActivity.showSnack(Utils.mainActivity.getString(R.string.connected));
            } else if (Download.status == 1 && viewUI) {
                MainActivity.showSnack(Utils.mainActivity.getString(R.string.noConnection));
            } else if (Download.status == 2 && viewUI) {
                MainActivity.showSnack(Utils.mainActivity.getString(R.string.nothingNew));
            }

            int i = Utils.mainActivity.mPager.getCurrentItem();

            Utils.mainActivity.startPagerView();
            Client.print("Pager started!");

            if (firstPagerStart) {
                Utils.mainActivity.setTable(getView());
                firstPagerStart = false;
            } else {
                Utils.mainActivity.setTable(i);
            }

            Utils.mainActivity.loadView.setVisibility(View.GONE);
            Utils.mainActivity.progBar.setEnabled(false);
            Utils.mainActivity.stdView.setVisibility(View.VISIBLE);

            Client.print("Pager visible");
        }
    }

    public static String getFormattedDate(Date date, boolean dayName, boolean useTime) {
        String pattern = "dd.MM.yyyy";
        if (useTime) pattern += " HH:mm";
        if (dayName) pattern = "EEEE " + pattern;

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
        if (SYSOUT) System.out.println(text);
    }

    public static void printMethod(String name) {
        if (METHOD_SYSOUT) System.out.println(name + "();");
    }
}