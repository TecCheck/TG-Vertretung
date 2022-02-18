package de.coop.tgvertretung.adapter;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

public class ScheduleFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ARG_ENTRY = "entry";
    public static final String ARG_INDEX = "index";

    public static Week week = null;

    private RecyclerView recyclerView;
    private TextView label;

    private int index;
    private Schedule schedule;
    private SubjectSymbols symbols;

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

        index = getArguments().getInt(ARG_INDEX);

        TgvApp app = (TgvApp) getActivity().getApplication();
        SettingsWrapper settings = app.getAppSettings();
        DataManager dataManager = app.getDataManager();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table, container, false);
        LifecycleOwner owner = getViewLifecycleOwner();

        dataManager.getSchedule(owner, false).observe(owner, newTimeTable -> {
            this.schedule = newTimeTable;
            trySetup(rootView);
        });

        dataManager.getSubjectSymbols(owner, false).observe(owner, symbols -> {
            this.symbols = symbols;
            trySetup(rootView);
        });

        return rootView;
    }

    private void trySetup(ViewGroup rootView) {
        if (symbols == null || schedule == null)
            return;

        label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);
        CardView layout2 = rootView.findViewById(R.id.layout2);
        layout2.setClickable(true);
        layout2.setFocusable(true);

        layout2.setOnClickListener(v -> {
            if (week.equals(Week.A)) {
                week = Week.B;
            } else {
                week = Week.A;
            }

            label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
            ScheduleEntryAdapter adapter = (ScheduleEntryAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();
        });

        label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
        label.setTextColor(getResources().getIntArray(R.array.day_of_week_color)[Math.min(index, 5)]);
        label2.setVisibility(View.INVISIBLE);
        label2.setHeight(14);
        nothing.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ScheduleEntryAdapter adapter = new ScheduleEntryAdapter(index, getContext(), schedule, symbols);
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getContext(), recyclerView, this);

        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) return;

        if (recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        if (label != null) {
            label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
        }
    }

    @Override
    public void onResume() {
        recyclerView.getAdapter().notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), ScheduleEditActivity.class);
        intent.putExtra(ARG_ENTRY, position);
        intent.putExtra(ARG_INDEX, index);

        startActivity(intent);
    }

    @Override
    public void onLongItemClick(View view, int position) {}
}