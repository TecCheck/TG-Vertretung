package de.coop.tgvertretung.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.sematre.tg.TimeTable;

public class Settings implements Serializable {

    private static final String PREFS_KEY = "settings";
    private static final long serialVersionUID = -6062123032432580842L;

    public static Settings settings = new Settings();
    public static SharedPreferences prefs = null;

    public TimeTable timeTable = new TimeTable(new Date(0), new ArrayList<>());
    public SubjectSymbols symbols = new SubjectSymbols();
    public NewTimeTable myNewTimeTable = new NewTimeTable();

    public static void load(Context context) {
        settings = new Settings();
        prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", 0);

        try {
            String settingsString = prefs.getString(PREFS_KEY, ObjectSerializer.serialize(settings));
            settings = (Settings) ObjectSerializer.deserialize(settingsString);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        settings.initVariables();
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

    public void initVariables() {
        if (myNewTimeTable == null) {
            myNewTimeTable = new NewTimeTable();
        }
    }
}