package de.coop.tgvertretung;

import android.content.SharedPreferences;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.coop.tgvertretung.utils.ObjectSerializer;
import de.coop.tgvertretung.utils.Statics;
import de.sematre.tg.Table;
import de.sematre.tg.TimeTable;

public class Settings implements Serializable{

    private static final String PREFS_KEY = "settings";
    private static final long serialVersionUID = -6062123032432580842L;

    public static Settings settings = new Settings();
    public static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor;

    public TimeTable timeTable = new TimeTable(new Date(System.currentTimeMillis()), new ArrayList<Table>());
    public Date lastClientRefresh = null;

    public String username = "";
    public String password = "";

    public boolean loggedIn = false;
    public boolean useFilter = false;
    public String filter = "";

    public boolean extended = false;
    public boolean showText = false;
    public boolean showAB = false;
    public boolean showClientRefresh = false;
    public boolean showServerRefresh = false;

    public boolean useOldLayout = false;
    public boolean rainbow = false;
    public boolean twoLineLabel = false;

    public static void load() {
        settings = new Settings();
        if (prefs == null) prefs = Statics.mainActivity.getSharedPreferences("preferences", 0);

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
            editor = prefs.edit();

            editor.putString(PREFS_KEY, ObjectSerializer.serialize(settings));
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