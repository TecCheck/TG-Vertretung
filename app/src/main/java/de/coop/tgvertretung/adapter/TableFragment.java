package de.coop.tgvertretung.adapter;

import android.animation.ArgbEvaluator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.storage.DataManager;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.coop.tgvertretung.utils.TgvApp;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;

public class TableFragment extends Fragment {

    public static ArgbEvaluator evaluator = null;
    private static final String ARG_INDEX = "index";

    private Table table;
    private SubjectSymbols symbols;

    public static TableFragment create(int index) {
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);

        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int index = getArguments().getInt(ARG_INDEX);
        TgvApp app = (TgvApp) getActivity().getApplication();
        SettingsWrapper settings = app.getAppSettings();
        DataManager dataManager = app.getDataManager();

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table, container, false);
        LifecycleOwner owner = getViewLifecycleOwner();

        dataManager.getTimeTable(owner, false).observe(owner, timeTable -> {
            if (timeTable != null)
                this.table = timeTable.getTables().get(index);
            trySetup(rootView, settings);
        });

        dataManager.getSubjectSymbols(owner, false).observe(owner, symbols -> {
            this.symbols = symbols;
            trySetup(rootView, settings);
        });

        return rootView;
    }

    private void trySetup(ViewGroup rootView, SettingsWrapper settings) {
        if (table == null || symbols == null)
            return;

        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        // Filer the table if needed
        Log.d("TableFragment" + getArguments().getInt(ARG_INDEX), "Filter: " + settings.getFilterEnabled() + " " + settings.getFilter());

        Table table = this.table;
        if (settings.getFilterEnabled())
            table = Utils.filterTable(this.table, settings.getFilter());

        if (evaluator == null) evaluator = new ArgbEvaluator();

        boolean isEmpty = table.getTableEntries().isEmpty();
        nothing.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        // Show nothing if table is empty
        if (isEmpty) {
            if (settings.getRainbow())
                Utils.addRainbow(nothing);
            else
                nothing.setTextColor(Utils.getColor(getContext(), table.getDate()));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new TableEntryAdapter(table, getContext(), settings, symbols));
        }

        // Add a rainbow effect to the label
        if (settings.getRainbow()) {
            Utils.addRainbow(label);
            if (settings.getTwoLineLabel()) Utils.addRainbow(label2);
        } else {
            label.setTextColor(Utils.getColor(getContext(), table.getDate()));
            if (settings.getTwoLineLabel())
                label2.setTextColor(Utils.getColor(getContext(), table.getDate()));
        }

        // Show the label
        if (settings.getTwoLineLabel()) {
            label.setText(getLabelTextPrim(table));
            label2.setVisibility(View.VISIBLE);
            label2.setText(getLabelTextSec(table, settings.getShowAb()));
        } else {
            label.setText(getLabelText(table, settings.getShowAb()));
            label2.setVisibility(View.INVISIBLE);
            label2.setHeight(14);
        }
    }

    private String getLabelText(Table table, boolean showAb) {
        String abcd = showAb ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();
        String week = getContext().getString(R.string.week, abcd);
        return Utils.getFormattedDate(table.getDate(), true, false) + " " + week;
    }

    private String getLabelTextPrim(Table table) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(table.getDate());
    }

    private String getLabelTextSec(Table table, boolean showAb) {
        String week = showAb ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(table.getDate()) + " " + getContext().getString(R.string.week, week);
    }
}