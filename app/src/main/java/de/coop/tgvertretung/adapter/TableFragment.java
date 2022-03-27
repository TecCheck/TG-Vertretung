package de.coop.tgvertretung.adapter;

import android.os.Bundle;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TableFragment extends BaseTableFragment {

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
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        LifecycleOwner owner = getViewLifecycleOwner();

        TgvApp app = (TgvApp) getActivity().getApplication();
        SettingsWrapper settings = app.getAppSettings();
        DataManager dataManager = app.getDataManager();

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

    private void trySetup(View rootView, SettingsWrapper settings) {
        if (table == null || symbols == null)
            return;

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        Log.d("TableFragment" + index, "Filter: " + settings.getFilterEnabled() + " " + settings.getFilter());

        Table table = this.table;
        if (settings.getFilterEnabled())
            table = Utils.filterTable(this.table, settings.getFilter());

        boolean isEmpty = table.getTableEntries().isEmpty();
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new TableEntryAdapter(table, getContext(), settings, symbols));
        }

        String labelText;
        String labelText2 = null;
        if (settings.getTwoLineLabel()) {
            labelText = getLabelTextPrim(table);
            labelText2 = getLabelTextSec(table, settings.getShowAb());
        } else {
            labelText = getLabelText(table, settings.getShowAb());
        }

        int colorIndex = Utils.getDayIndexOfDate(table.getDate());
        setupUi(rootView, colorIndex, settings.getRainbow(), labelText, labelText2, isEmpty);
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