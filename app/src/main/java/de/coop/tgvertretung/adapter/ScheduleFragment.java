package de.coop.tgvertretung.adapter;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.ScheduleEditActivity;
import de.coop.tgvertretung.storage.DataManager;
import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.coop.tgvertretung.utils.TgvApp;
import de.sematre.tg.Week;

public class ScheduleFragment extends BaseTableFragment implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ARG_ENTRY = "entry";
    public static Week globalWeek = null;

    private Week localWeek;
    private Schedule schedule;
    private SubjectSymbols symbols;

    private TextView label;
    private RecyclerView recyclerView;

    public static ScheduleFragment create(int index) {
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TgvApp app = (TgvApp) getActivity().getApplication();
        SettingsWrapper settings = app.getAppSettings();
        DataManager dataManager = app.getDataManager();
        LifecycleOwner owner = getViewLifecycleOwner();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table, container, false);
        label = rootView.findViewById(R.id.label);

        localWeek = globalWeek;

        dataManager.getSchedule(owner, false).observe(owner, schedule -> {
            this.schedule = schedule;
            trySetup(rootView, settings);
        });

        dataManager.getSubjectSymbols(owner, false).observe(owner, symbols -> {
            this.symbols = symbols;
            trySetup(rootView, settings);
        });

        return rootView;
    }

    private void trySetup(ViewGroup rootView, SettingsWrapper settings) {
        if (symbols == null || schedule == null)
            return;

        CardView layout2 = rootView.findViewById(R.id.layout2);
        layout2.setClickable(true);
        layout2.setFocusable(true);
        layout2.setOnClickListener(v -> {
            localWeek = globalWeek = globalWeek.equals(Week.A) ? Week.B : Week.A;
            label.setText(getLabelText());
            ScheduleEntryAdapter adapter = (ScheduleEntryAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ScheduleEntryAdapter adapter = new ScheduleEntryAdapter(index, getContext(), schedule, symbols);
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getContext(), recyclerView, this);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String labelText = getLabelText();
        setupUi(rootView, index, settings.getRainbow(), labelText, null, false);
    }

    private String getLabelText() {
        return getResources().getStringArray(R.array.days)[index] + " " + localWeek.getLetter();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globalWeek.equals(localWeek))
            return;

        localWeek = globalWeek;
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();

        if (label != null)
            label.setText(getResources().getStringArray(R.array.days)[index] + " " + localWeek.getLetter());
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), ScheduleEditActivity.class);
        intent.putExtra(ARG_ENTRY, position);
        intent.putExtra(ARG_INDEX, index);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(View view, int position) {
    }
}
