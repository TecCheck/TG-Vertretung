package de.coop.tgvertretung.adapter;

import android.app.Dialog;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
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
        Spinner spinner = dialog.findViewById(R.id.spinner);
        spinner.setAdapter(new SpinnerAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == spinner.getAdapter().getCount() - 1){
                    spinner.setVisibility(View.GONE);
                    EditText symbol = dialog.findViewById(R.id.editTextSubject);
                    EditText name = dialog.findViewById(R.id.editTextSubjectName);
                    name.setVisibility(View.VISIBLE);
                    symbol.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button button = dialog.findViewById(R.id.buttonAdd);
        Button removeButton = dialog.findViewById(R.id.buttonRemove);
        button.setOnClickListener(v -> {

            EditText subject = dialog.findViewById(R.id.editTextSubject);
            EditText subjectName = dialog.findViewById(R.id.editTextSubjectName);
            EditText room = dialog.findViewById(R.id.editTextRoom);
            EditText teacher = dialog.findViewById(R.id.editTextTeacher);

            String subjectS = "";

            if(subject.getVisibility() == View.VISIBLE){
                subjectS = subject.getText().toString();
                Settings.settings.symbols.setSymbol(subjectS, subjectName.getText().toString());
            }else {
                subjectS = Settings.settings.symbols.getSymbol(spinner.getSelectedItemPosition());
            }

            TimeTable.TimeTableDay day = Settings.settings.myTimeTable.getDay(index);
            day.setEntry(position, new TimeTable.TimeTableEntry(subjectS, room.getText().toString(), teacher.getText().toString()));
            Settings.settings.myTimeTable.setDay(index, day);
            try {
                recyclerView.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeTable.TimeTableDay day = Settings.settings.myTimeTable.getDay(index);
                day.setEntry(position, null);
                Settings.settings.myTimeTable.setDay(index, day);
                try {
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
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

    class SpinnerAdapter implements android.widget.SpinnerAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.symbol_entry_item, parent, false);
            TextView textView = view.findViewById(R.id.textView);
            if(position != Settings.settings.symbols.getCount()){
                view.findViewById(R.id.imageView).setVisibility(View.GONE);
                textView.setText(Settings.settings.symbols.getSymbolName(position));
            }else {
                view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                textView.setText(R.string.add);
            }
            return view;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return Settings.settings.symbols.getCount() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.symbol_entry_item, parent, false);
            TextView textView = view.findViewById(R.id.textView);
            if(position != Settings.settings.symbols.getCount()){
                view.findViewById(R.id.imageView).setVisibility(View.GONE);
                textView.setText(Settings.settings.symbols.getSymbolName(position));
            }else {
                view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                textView.setText(R.string.add);
            }
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}