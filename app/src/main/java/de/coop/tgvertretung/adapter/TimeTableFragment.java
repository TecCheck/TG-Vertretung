package de.coop.tgvertretung.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.activity.TimeTableEditActivity;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;

public class TimeTableFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    public static final String ENTRY_INDEX = "entry";
    public static final String INDEX = "index";
    public int index;
    RecyclerView recyclerView;
    boolean isAWeek;

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
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);
        isAWeek = isAWeek();

        label.setClickable(true);
        label.setOnClickListener(v -> {
            isAWeek = !isAWeek;
            label.setText(getResources().getStringArray(R.array.days)[index] + " " + (isAWeek ? getString(R.string.week_a) : getString(R.string.week_b)));
            TimeTableEntryAdapter adapter = (TimeTableEntryAdapter) recyclerView.getAdapter();
            adapter.isAWeek = isAWeek;
            adapter.notifyDataSetChanged();
        });

        // Get index
        index = getArguments().getInt(INDEX);

        Log.d("Week", "A: " + isAWeek);

        label.setText(getResources().getStringArray(R.array.days)[index] + " " + (isAWeek ? getString(R.string.week_a) : getString(R.string.week_b)));
        label.setTextColor(getResources().getIntArray(R.array.day_of_week_color)[Math.min(index, 5)]);
        label2.setVisibility(View.INVISIBLE);
        label2.setHeight(14);
        nothing.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        TimeTableEntryAdapter adapter = new TimeTableEntryAdapter(index, getContext());
        adapter.isAWeek = isAWeek;
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getContext(), recyclerView, this);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    boolean isAWeek() {
        int i = Utils.getView(Settings.settings.timeTable, Settings.settings.timeTable.getTables().size() - 1);
        try{
            return Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("A") || Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("C");

        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return true;
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