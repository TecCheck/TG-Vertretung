package de.coop.tgvertretung.adapter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.TimeTable;
import de.coop.tgvertretung.utils.Utils;

public class TimeTableFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    private static final String INDEX = "index";
    public int index;
    Dialog dialog;
    RecyclerView recyclerView;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main2, container, false);
        TextView label = rootView.findViewById(R.id.label);
        TextView label2 = rootView.findViewById(R.id.label2);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView nothing = rootView.findViewById(R.id.nothing_to_show);

        // Get index
        index = getArguments().getInt(INDEX);

        if (Settings.settings.myTimeTable == null) {
            Settings.settings.myTimeTable = new TimeTable();
        }

        label.setText(getResources().getStringArray(R.array.days)[index]);
        label.setTextColor(getResources().getIntArray(R.array.day_of_week_color)[Math.min(index, 5)]);
        label2.setVisibility(View.INVISIBLE);
        label2.setHeight(14);
        nothing.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.Adapter adapter = new TimeTableEntryAdapter(Settings.settings.myTimeTable.getDay(index), index, getContext());
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getContext(), recyclerView, this);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("onItemClikc", "Recievd");
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_edit_class);
        ImageButton imageButton = dialog.findViewById(R.id.imageButtonAdd);
        imageButton.setOnClickListener(v -> {

            EditText subject = dialog.findViewById(R.id.editTextSubject);
            EditText room = dialog.findViewById(R.id.editTextRoom);
            EditText teacher = dialog.findViewById(R.id.editTextTeacher);

            TimeTable.TimeTableDay day = Settings.settings.myTimeTable.getDay(index);
            if (subject.getText().toString().equals("") && room.getText().toString().equals("") && teacher.getText().toString().equals("")){
                day.setEntry(position, null);
            }else {
                day.setEntry(position, new TimeTable.TimeTableEntry(subject.getText().toString(), room.getText().toString(), teacher.getText().toString()));
            }
            Settings.settings.myTimeTable.setDay(index, day);
            try {
                recyclerView.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        TimeTable.TimeTableEntry entry = Settings.settings.myTimeTable.getDay(index).getEntry(position);
        if (entry != null) {
            EditText subject = dialog.findViewById(R.id.editTextSubject);
            EditText room = dialog.findViewById(R.id.editTextRoom);
            EditText teacher = dialog.findViewById(R.id.editTextTeacher);
            subject.setText(entry.getSubject());
            room.setText(entry.getRoom());
            teacher.setText(entry.getTeacher());
        }

        dialog.show();
    }

    @Override
    public void onLongItemClick(View view, int position) {

    }
}