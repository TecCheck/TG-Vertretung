package de.coop.tgvertretung;

import android.content.SharedPreferences;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.sematre.tg.Table;
import de.sematre.tg.TimeTable;

public class Settings implements Serializable{

    public static final String PREFS_KEY = "settings";
    private static final long serialVersionUID = -6062123032432580842L;

    public static Settings settings = new Settings();
    public static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor;

    public TimeTable timeTable = new TimeTable(new Date(System.currentTimeMillis()), new ArrayList<Table>());
    public Date lastClientRefresh = null;
    public String username = "";
    public String password = "";
    public String filter = "";
    public boolean useFilter = false;
    public boolean extended = false;
    public boolean showText = false;
    public boolean loggedIn = false;
    public boolean showClientRefresh = true;
    public boolean showServerRefresh = false;
    public boolean showAB = false;

    public static void load() {
        settings = new Settings();
        if (prefs == null)
            prefs = MainActivity.instance.getSharedPreferences("preferences", 0);
        try {
            String s = ObjectSerializer.serialize(settings);
            s = prefs.getString(PREFS_KEY, s);
            settings = (Settings) ObjectSerializer.deserialize(s);
            print();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            editor = prefs.edit();
            String s = ObjectSerializer.serialize(settings);
            editor.putString(PREFS_KEY, s);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(){
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

        Client.print(out);
    }
}
