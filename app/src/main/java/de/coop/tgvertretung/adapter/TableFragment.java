package de.coop.tgvertretung.adapter;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;

public class TableFragment extends Fragment {

    private static final String INDEX = "index";
    public static ArgbEvaluator evaluator = null;

    private Table table = null;

    public static TableFragment newInstance(int sectionNumber) {
        Utils.printMethod("newInstance");

        Bundle args = new Bundle();
        args.putInt(INDEX, sectionNumber);

        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.printMethod("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        // Filer the table if needed
        int index = getArguments().getInt(INDEX);
        Table t = Settings.settings.timeTable.getTables().get(index);
        if (Settings.settings.useFilter) {
            table = Utils.filterTable(t, Settings.settings.filter);
        } else {
            table = t;
        }

        if (evaluator == null) evaluator = new ArgbEvaluator();

        // Show nothing if table is empty
        if (table.getTableEntries().isEmpty()) {
            if (Settings.settings.rainbow) Utils.addRainbow(nothing);
            else nothing.setTextColor(Utils.getColor(getContext(), table.getDate()));

            nothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            nothing.setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
            RecyclerView.Adapter adapter = new TableEntryAdapter(table, getContext());

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        // Add a rainbow effect to the label
        if (Settings.settings.rainbow) {
            Utils.addRainbow(label);
            if (Settings.settings.twoLineLabel) Utils.addRainbow(label2);
        } else {
            label.setTextColor(Utils.getColor(getContext(), table.getDate()));
            if (Settings.settings.twoLineLabel)
                label2.setTextColor(Utils.getColor(getContext(), table.getDate()));
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
        String week = getContext().getString(R.string.week) + " ";
        week += Settings.settings.showAB ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();

        return Utils.getFormattedDate(table.getDate(), true, false) + " " + week;
    }

    private String getLabelTextPrim() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(table.getDate());
    }

    private String getLabelTextSec() {
        String week = Settings.settings.showAB ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(table.getDate()) + " " + (week + " " + getContext().getString(R.string.week));
    }
}