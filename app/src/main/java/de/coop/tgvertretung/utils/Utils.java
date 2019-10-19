package de.coop.tgvertretung.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Environment;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TableFragment;
import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;
import de.sematre.tg.TimeTable;

public class Utils {

    private static final boolean SYSOUT = true;
    private static final boolean METHOD_SYSOUT = true;

    private static ArgbEvaluator evaluator = null;

    public static int getColor(Context context, Date date) {
        Utils.printMethod("setColor");

        int dayOfWeek = date.getDay() - 1;
        return context.getResources().getIntArray(R.array.day_of_week_color)[Math.min(dayOfWeek, 5)];
    }

    public static ArrayList<TableEntry> filterTable(ArrayList<TableEntry> entries, String filter) {
        ArrayList<TableEntry> filtered = new ArrayList<>();
        for (TableEntry entry : entries) {
            if (entry.getSchoolClass().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(entry);
            }
        }

        return filtered;
    }

    public static Table filterTable(Table table, String filter) {
        ArrayList<TableEntry> entries = table.getTableEntries();
        ArrayList<TableEntry> filtered = new ArrayList<>();
        for (TableEntry entry : entries) {
            if (entry.getSchoolClass().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(entry);
            }
        }

        Table table1 = new Table();
        table1.setDate(table.getDate());
        table1.setWeek(table.getWeek());
        table1.setTableEntries(filtered);
        return table1;
    }

    public static void addRainbow(View view) {
        if (evaluator == null) evaluator = new ArgbEvaluator();

        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "textColor", TableFragment.evaluator, 0xff0000ff, 0xffff0000, 0xff00ff00);
        colorFade.setRepeatMode(ObjectAnimator.REVERSE);
        colorFade.setRepeatCount(ObjectAnimator.INFINITE);
        colorFade.setDuration(1200);
        colorFade.start();
    }

    public static String getFormattedDate(Date date, boolean dayName, boolean useTime) {
        String pattern = "dd.MM.yyyy";
        if (useTime) pattern += " HH:mm";
        if (dayName) pattern = "EEEE " + pattern;

        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static int getView(TimeTable timeTable, int defaultValue) {
        Utils.printMethod("getView");

        int i = 0;
        for (Table table : timeTable.getTables()) {
            Date today = new Date(System.currentTimeMillis());
            Date tableDate = table.getDate();
            if (today.getDay() == tableDate.getDay() && today.getMonth() == tableDate.getMonth() && today.getYear() == tableDate.getYear()) {
                return i;
            }

            Utils.print(table.getDate().toString());
            i++;
        }

        Utils.print("Date not found");
        return defaultValue;
    }

    public static int getView(TimeTable timeTable) {
        return getView(timeTable, 0);
    }

    public static String getUpdateDownloadFile(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + "/TGV.apk";
    }

    public static void print(String text) {
        if (SYSOUT) System.out.println(text);
    }

    public static void printMethod(String name) {
        if (METHOD_SYSOUT) System.out.println(name + "();");
    }
}