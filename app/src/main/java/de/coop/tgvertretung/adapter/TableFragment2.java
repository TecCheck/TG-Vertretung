package de.coop.tgvertretung.adapter;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.R;
import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;
import de.sematre.tg.Week;

public class TableFragment2 extends Fragment {

    private static final String INDEX = "index";
    public static ArgbEvaluator evaluator = null;
    private Table table = null;

    public static TableFragment2 newInstance(int sectionNumber) {
        Client.printMethod("newInstance");

        Bundle args = new Bundle();
        args.putInt(INDEX, sectionNumber);

        TableFragment2 fragment = new TableFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Client.printMethod("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main2, container, false);
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        // Filer the table if needed
        int index = getArguments().getInt(INDEX);
        if (Settings.settings.useFilter) {
            table = Utils.filterTable(Settings.settings.timeTable.getTables().get(index), Settings.settings.filter);
        } else {
            table = Settings.settings.timeTable.getTables().get(index);
        }

        if (evaluator == null) evaluator = new ArgbEvaluator();

        // Show nothing if table is empty
        if (table.getTableEntries().isEmpty()) {
            if (Settings.settings.rainbow) Utils.addRainbow(nothing);
            else nothing.setTextColor(Utils.getColor(table.getDate()));

            nothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            nothing.setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Utils.mainActivity.getApplicationContext());
            RecyclerView.Adapter adapter = new TableEntryAdapter(table);

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        // Add a rainbow effect to the label
        if (Settings.settings.rainbow) {
            Utils.addRainbow(label);
            if (Settings.settings.twoLineLabel) Utils.addRainbow(label2);
        } else {
            label.setTextColor(Utils.getColor(table.getDate()));
            if (Settings.settings.twoLineLabel)
                label2.setTextColor(Utils.getColor(table.getDate()));
        }

        // Show the label
        if (Settings.settings.twoLineLabel) {
            label.setText(getLabelTextPrim());
            label2.setVisibility(View.VISIBLE);
            label2.setText(getLabelTextSec());
        } else {
            label.setText(getLabelText());
            label2.setVisibility(View.INVISIBLE);
            label2.setHeight(14);
        }

        return rootView;
    }

    private String getLabelText() {
        String week = Utils.mainActivity.getString(R.string.week) + " ";
        if (Settings.settings.showAB)
            week += (table.getWeek() == Week.A || table.getWeek() == Week.C) ? "A" : "B";
        else week += table.getWeek().getLetter();

        return Client.getFormattedDate(table.getDate(), true, false) + " " + week;
    }

    private String getLabelTextPrim() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(table.getDate());
    }

    private String getLabelTextSec() {
        String week = null;
        if (Settings.settings.showAB)
            week = (table.getWeek() == Week.A || table.getWeek() == Week.C) ? "A" : "B";
        else week = table.getWeek().getLetter();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(table.getDate()) + " " + (week + " " + Utils.mainActivity.getString(R.string.week));
    }
}