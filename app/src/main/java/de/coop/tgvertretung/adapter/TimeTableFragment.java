package de.coop.tgvertretung.adapter;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.TimeTableEditActivity;
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.Week;

public class TimeTableFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ENTRY_INDEX = "entry";
    public static final String INDEX = "index";

    public static Week week = null;

    private final int index;
    private final SubjectSymbols symbols;
    private final NewTimeTable newTimeTable;

    private RecyclerView recyclerView;
    private TextView label;

    public TimeTableFragment(NewTimeTable newTimeTable, SubjectSymbols symbols, int index) {
        super();
        this.index = index;
        this.symbols = symbols;
        this.newTimeTable = newTimeTable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_table, container, false);
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
            TimeTableEntryAdapter adapter = (TimeTableEntryAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();
        });

        label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
        label.setTextColor(getResources().getIntArray(R.array.day_of_week_color)[Math.min(index, 5)]);
        label2.setVisibility(View.INVISIBLE);
        label2.setHeight(14);
        nothing.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        TimeTableEntryAdapter adapter = new TimeTableEntryAdapter(index, getContext(), newTimeTable, symbols);
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getContext(), recyclerView, this);

        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
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
        Intent intent = new Intent(getContext(), TimeTableEditActivity.class);
        intent.putExtra(ENTRY_INDEX, position);
        intent.putExtra(INDEX, index);

        startActivity(intent);
    }

    @Override
    public void onLongItemClick(View view, int position) {}
}