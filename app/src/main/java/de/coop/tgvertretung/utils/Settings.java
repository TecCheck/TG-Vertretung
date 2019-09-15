package de.coop.tgvertretung.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.sematre.tg.Table;
import de.sematre.tg.TimeTable;

public class Settings implements Serializable {

    private static final String PREFS_KEY = "settings";
    private static final long serialVersionUID = -6062123032432580842L;

    public static Settings settings = new Settings();
    public static SharedPreferences prefs = null;

    public TimeTable timeTable = new TimeTable(new Date(System.currentTimeMillis()), new ArrayList<>());
    public Date lastClientRefresh = null;

    public String username = "";
    public String password = "";

    public boolean loggedIn = false;
    public boolean useFilter = false;
    public String filter = "";

    public boolean extended = false;
    public boolean showText = true;
    public boolean showAB = false;
    public boolean showClientRefresh = true;
    public boolean showServerRefresh = true;

    public int themeMode = 0;
    public boolean rainbow = false;
    public boolean twoLineLabel = false;

    public ClassSymbols symbols = new ClassSymbols();
    public de.coop.tgvertretung.utils.TimeTable myTimeTable = new de.coop.tgvertretung.utils.TimeTable();

    public static void load(Context context) {
        settings = new Settings();
        prefs = context.getSharedPreferences("preferences", 0);

        try {
            String settingsString = prefs.getString(PREFS_KEY, ObjectSerializer.serialize(settings));
            settings = (Settings) ObjectSerializer.deserialize(settingsString);
            print();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(PREFS_KEY, ObjectSerializer.serialize(settings));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print() {
        String out = "Settings: \n";
        //out += "timeTable: " + settings.timeTable;
        out += ", lastClientRefresh: " + settings.lastClientRefresh;
        out += ", username: " + settings.username;
        out += ", password: " + settings.password;
        out += ", filter: " + settings.filter;
        out += ", useFilter: " + settings.useFilter;
        out += ", extended: " + settings.extended;
        out += ", loggedIn: " + settings.loggedIn;
        out += ", showText: " + settings.showText;
        out += ", showClientRefresh: " + settings.showClientRefresh;
        out += ", showServerRefresh: " + settings.showServerRefresh;
        out += ", showAB" + settings.showAB;

        Utils.print(out);
    }
}