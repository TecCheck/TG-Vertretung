package de.coop.tgvertretung.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.TimeTableEditActivity;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Week;

public class TimeTableFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ENTRY_INDEX = "entry";
    public static final String INDEX = "index";
    public int index;
    RecyclerView recyclerView;
    TextView label;

    public static Week week = null;

    public static TimeTableFragment newInstance(int sectionNumber) {
        Utils.printMethod("newInstance");

        Bundle args = new Bundle();
        args.putInt(INDEX, sectionNumber);

        TimeTableFragment fragment = new TimeTableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.printMethod("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        // Get the Views
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);
        CardView layout2 = rootView.findViewById(R.id.layout2);
        layout2.setClickable(true);
        layout2.setFocusable(true);

        layout2.setOnClickListener(v -> {
            if(week.equals(Week.A))
                week = Week.B;
            else
                week = Week.A;
            label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
            TimeTableEntryAdapter adapter = (TimeTableEntryAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();
        });

        // Get index
        index = getArguments().getInt(INDEX);

        Log.d("Week", "A: " + week);

        label.setText(getResources().getStringArray(R.array.days)[index] + " " + week.getLetter());
        label.setTextColor(getResources().getIntArray(R.array.day_of_week_color)[Math.min(index, 5)]);
        label2.setVisibility(View.INVISIBLE);
        label2.setHeight(14);
        nothing.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        TimeTableEntryAdapter adapter = new TimeTableEntryAdapter(index, getContext());
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

        if(isVisibleToUser){
            if(recyclerView != null && recyclerView.getAdapter() != null)
                recyclerView.getAdapter().notifyDataSetChanged();
            if(label != null)
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
    public void onLongItemClick(View view, int position) {

    }
}