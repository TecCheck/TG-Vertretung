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
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;

public class TableFragment extends Fragment {

    public static ArgbEvaluator evaluator = null;

    private final int index;
    private final SettingsWrapper settings;

    public TableFragment(int index, SettingsWrapper settings) {
        this.index = index;
        this.settings = settings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table, container, false);
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        // Filer the table if needed
        Table table = Settings.settings.timeTable.getTables().get(index);

        if (settings.getFilterEnabled())
            table = Utils.filterTable(table, settings.getFilter());

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
            recyclerView.setAdapter(new TableEntryAdapter(table, getContext(), settings));
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
            label2.setText(getLabelTextSec(table));
        } else {
            label.setText(getLabelText(table));
            label2.setVisibility(View.INVISIBLE);
            label2.setHeight(14);
        }

        return rootView;
    }

    private String getLabelText(Table table) {
        String abcd = settings.getShowAb() ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();
        String week = getContext().getString(R.string.week, abcd);
        return Utils.getFormattedDate(table.getDate(), true, false) + " " + week;
    }

    private String getLabelTextPrim(Table table) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(table.getDate());
    }

    private String getLabelTextSec(Table table) {
        String week = settings.getShowAb() ? table.getWeek().getSimplifiedLetter() : table.getWeek().getLetter();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(table.getDate()) + " " + getContext().getString(R.string.week, week);
    }
}