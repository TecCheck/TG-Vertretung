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
    public boolean extendet = false;
    public boolean showText = false;
    public boolean loggedIn = false;
    public boolean showClientRefersh = true;
    public boolean showServerRefresh = false;

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
        //out = out + "timeTable: " + settings.timeTable;
        out = out + ", lastClientRefresh: " + settings.lastClientRefresh;
        out = out + ", username: " + settings.username;
        out = out + ", password: " + settings.password;
        out = out + ", filter: " + settings.filter;
        out = out + ", useFilter: " + settings.useFilter;
        out = out + ", extendet: " + settings.extendet;
        out = out + ", loggedIn: " + settings.loggedIn;
        out = out + ", showText: " + settings.showText;
        out = out + ", showClientRefersh: " + settings.showClientRefersh;
        out = out + ", showServerRefresh: " + settings.showServerRefresh;


        Client.print(out);
    }
}
