package de.coop.tgvertretung;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.activity.MainActivity;
import de.coop.tgvertretung.utils.Download;
import de.coop.tgvertretung.utils.Statics;
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
    public static SwipeRefreshLayout refreshLayout = null;

    public Client() {
        if (instance == null) {
            instance = this;
        }
    }

    public static void load(boolean view) {
        Client.printMethod("loadFinished");
        viewUI = view;

        if (view && false) {
            Statics.mainActivity.stdView.setVisibility(View.INVISIBLE);
            Statics.mainActivity.loadView.setVisibility(View.VISIBLE);
            Statics.mainActivity.progBar.setEnabled(true);
        }

        if (dwdThread == null || !dwdThread.isAlive()) {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            dwdThread.start();
        }
    }

    public static void loadFinished() {
        Client.printMethod("loadFinished");

        if(refreshLayout != null){
            refreshLayout.setRefreshing(false);
            refreshLayout = null;
        }

        if(viewUI){
            if (Download.status == 0 && viewUI) {
                MainActivity.showSnack(Statics.mainActivity.getString(R.string.connected));
            } else if (Download.status == 1 && viewUI) {
                MainActivity.showSnack(Statics.mainActivity.getString(R.string.noConnection));
            }else if(Download.status == 2 && viewUI){
                MainActivity.showSnack(Statics.mainActivity.getString(R.string.nothingNew));
            }
        }
        if (Download.status == 0) {
            Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
        }
        if (viewUI) {
            int i = Statics.mainActivity.mPager.getCurrentItem();
            Statics.mainActivity.startPagerView();
            Client.print("Pager started!");
            if (firstPagerStart) {
                Statics.mainActivity.setTable(getView());
                firstPagerStart = false;
            } else
                Statics.mainActivity.setTable(i);
        }

        if (viewUI) {
            Statics.mainActivity.loadView.setVisibility(View.GONE);
            Statics.mainActivity.progBar.setEnabled(false);
            Statics.mainActivity.stdView.setVisibility(View.VISIBLE);
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