package de.coop.tgvertretung.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TableFragment2;
import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;

public class Utils {

    public static ArgbEvaluator evaluator = null;

    public static int getColor(Date date) {
        Client.printMethod("setColor");

        switch (date.getDay()){
            case 1:
                return Statics.mainActivity.getResources().getColor(R.color.yellow);

            case 2:
                return Statics.mainActivity.getResources().getColor(R.color.blue);

            case 3:
                return Statics.mainActivity.getResources().getColor(R.color.green);

            case 4:
                return Statics.mainActivity.getResources().getColor(R.color.orange);

            case 5:
                return Statics.mainActivity.getResources().getColor(R.color.pink);

            default:
                return Statics.mainActivity.getResources().getColor(R.color.purple);
        }
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

        table.setTableEntries(filtered);
        return table;
    }

    public static void addRainbow(View view){
        if (evaluator == null) evaluator = new ArgbEvaluator();

        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "textColor", TableFragment2.evaluator, 0xff0000ff,0xffff0000,0xff00ff00);
        colorFade.setRepeatMode(ObjectAnimator.REVERSE);
        colorFade.setRepeatCount(ObjectAnimator.INFINITE);
        colorFade.setDuration(1200);
        colorFade.start();
    }
}