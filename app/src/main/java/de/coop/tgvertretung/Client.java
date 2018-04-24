package de.coop.tgvertretung;

import android.content.SharedPreferences;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.sematre.api.tg.VertretungsTabelle;

public class Client {
    private static final boolean SYSOUT = true;
    public static Client instance = null;
    public static ArrayList<VertretungsTabelle> tables = new ArrayList<>();
    public static String filter = "";
    //public static int filter = -1;
    public static boolean saveOfflineBool = true;
    public static boolean extendet = false;
    public static boolean useFilter = false;
    public static String password = "";
    public static String username = "";
    public static boolean singInConfirmed = false;
    public static String lastReloadStr = "";
    public static String lastserverRefreshStr = "";
    public static boolean login = false;
    private static boolean viewUI = false;
    private static  boolean firstTime = true;
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
        viewUI = view;

        if (view) {
            MainActivity.instance.stdView.setVisibility(View.INVISIBLE);
            MainActivity.instance.loadView.setVisibility(View.VISIBLE);
            MainActivity.instance.progBar.setEnabled(true);
        }

        if (dwdThread != null && dwdThread.isAlive()) {

        } else {
            dwdThread = new Thread(new Download());
            dwdThread.setName("Download-Thread");
            dwdThread.start();
        }

    }

    public static void loadFinished() {

        Client.print("viewUI: " + viewUI);

        Client.print("load Finished");
        //list is loading
        try {

            Client.print("online: " + Download.online);

            if (Download.online) {
                if (viewUI)
                    MainActivity.showSnack(MainActivity.instance.getString(R.string.connected));

                lastReloadStr = CurrentTime(false);

                if (saveOfflineBool) {
                    SharedPreferences.Editor tableEdit = MainActivity.instance.table.edit();

                    tableEdit.putString(MainActivity.instance.getString(R.string.tab_time), lastReloadStr);
                    tableEdit.putString(MainActivity.instance.getString(R.string.tab_servertime), lastserverRefreshStr);
                    tableEdit.putString(MainActivity.instance.getString(R.string.tab_tables), ObjectSerializer.serialize(tables));

                    tableEdit.apply();
                }
            } else {

                if (viewUI) {
                    MainActivity.showSnack(MainActivity.instance.getString(R.string.noConnection));
                }


                if (saveOfflineBool) {
                    lastReloadStr = MainActivity.instance.table.getString(MainActivity.instance.getString(R.string.tab_time), MainActivity.instance.getString(R.string.never));
                    lastserverRefreshStr = MainActivity.instance.table.getString(MainActivity.instance.getString(R.string.tab_servertime), MainActivity.instance.getString(R.string.never));
                    tables = (ArrayList<VertretungsTabelle>) ObjectSerializer.deserialize(MainActivity.instance.table.getString(MainActivity.instance.getString(R.string.tab_tables), ObjectSerializer.serialize(tables)));

                }

                Client.print("table: " + tables);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Client.print("Error!!!");
        }

        if (viewUI) {
            int i = MainActivity.mPager.getCurrentItem();
            MainActivity.instance.startPagerView();
            Client.print("Pager started!");
            if(firstTime) {
                MainActivity.instance.setTable(getView(CurrentTime(true)));
                firstTime = false;
            }else
               MainActivity.instance.setTable(i);

            System.out.println(getView(CurrentTime(true)));
        }

        Download.online = true;
        if (viewUI) {
            MainActivity.instance.loadView.setVisibility(View.GONE);
            MainActivity.instance.progBar.setEnabled(false);
            MainActivity.instance.stdView.setVisibility(View.VISIBLE);
            Client.print("Pager vissible");
        }

    }

    public static String CurrentTime(boolean onlyDate) {
        Calendar calendar = new GregorianCalendar();
        String str = calendar.get(Calendar.DAY_OF_MONTH ) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR) + " ";
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

    public static void print(String text) {
        if (SYSOUT)
            System.out.println(text);
    }
}
