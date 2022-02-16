package de.coop.tgvertretung.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Environment;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TableFragment;
import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;
import de.sematre.tg.TimeTable;

public class Utils {

    private static ArgbEvaluator evaluator = null;

    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static int getColor(Context context, Date date) {
        int dayOfWeek = getDayOfWeek(date) - 1;
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

    public static String getRelativeFormattedTime(Date date, Date referenceDate) {
        long difference = referenceDate.getTime() - date.getTime();
        if (difference < 0) { // Future
            return App.getAppResources().getString(R.string.time_future);
        } else if (difference < (60L * 1000L)) { // 1 Minute
            return App.getAppResources().getString(R.string.time_lessThenAMinute);
        } else if (difference < (2L * 60L * 1000L)) { // 2 Minutes
            return App.getAppResources().getString(R.string.time_aMinuteAgo);
        } else if (difference < (60L * 60L * 1000L)) { // 60 Minutes
            return String.format(App.getAppResources().getString(R.string.time_nMinutesAgo), String.valueOf(difference / 1000L / 60L));
        }

        Calendar calendar = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();
        Calendar calendarYesterday = Calendar.getInstance();
        Calendar calendarWeekAgo = Calendar.getInstance();

        calendar.setTime(date);
        calendarToday.setTime(referenceDate);
        calendarYesterday.setTime(referenceDate);
        calendarWeekAgo.setTime(referenceDate);

        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1);
        calendarWeekAgo.add(Calendar.DAY_OF_YEAR, -6);

        String prefix;
        if ((calendar.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) && (calendar.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR))) {
            prefix = App.getAppResources().getString(R.string.time_today);
        } else if ((calendar.get(Calendar.YEAR) == calendarYesterday.get(Calendar.YEAR)) && (calendar.get(Calendar.DAY_OF_YEAR) == calendarYesterday.get(Calendar.DAY_OF_YEAR))) {
            prefix = App.getAppResources().getString(R.string.time_yesterday);
        } else if (calendar.after(calendarWeekAgo)) {
            prefix = new SimpleDateFormat("EEEE", Locale.getDefault()).format(date);
        } else {
            prefix = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date);
        }

        return prefix + ", " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public static int getView(TimeTable timeTable, int defaultValue) {
        int i = 0;
        for (Table table : timeTable.getTables()) {
            Date today = new Date(System.currentTimeMillis());
            Date tableDate = table.getDate();
            if (getDayOfWeek(today) == getDayOfWeek(tableDate) && getMonth(today) == getMonth(tableDate) && getYear(today) == getYear(tableDate)) {
                return i;
            }
            i++;
        }
        return defaultValue;
    }

    public static int getView(TimeTable timeTable) {
        return getView(timeTable, 0);
    }

    public static String getUpdateDownloadFile(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + "/TGV.apk";
    }

    public static String checkEmptyString(String string) {
        return (string != null && string.isEmpty()) ? App.getAppResources().getString(R.string.no_infos) : string;
    }
}