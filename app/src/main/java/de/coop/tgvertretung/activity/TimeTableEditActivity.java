package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TimeTableFragment;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.TimeTable;

public class TimeTableEditActivity extends AppCompatActivity {

    Spinner spinnerA;
    EditText editSubjectA;
    EditText editSubjectNameA;
    EditText editTeacherA;
    EditText editRoomA;

    Spinner spinnerB;
    EditText editSubjectB;
    EditText editSubjectNameB;
    EditText editTeacherB;
    EditText editRoomB;

    CheckBox checkBoxDouble;
    Button button;
    Button removeButton;

    int entryIndex;
    int dayIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_edit);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        entryIndex = intent.getIntExtra(TimeTableFragment.ENTRY_INDEX, 0);
        dayIndex = intent.getIntExtra(TimeTableFragment.INDEX, 0);

        spinnerA = findViewById(R.id.spinner);
        editSubjectA = findViewById(R.id.editTextSubject);
        editSubjectNameA = findViewById(R.id.editTextSubjectName);
        editTeacherA = findViewById(R.id.editTextTeacher);
        editRoomA = findViewById(R.id.editTextRoom);

        spinnerB = findViewById(R.id.spinnerB);
        editSubjectB = findViewById(R.id.editTextSubjectB);
        editSubjectNameB = findViewById(R.id.editTextSubjectNameB);
        editTeacherB = findViewById(R.id.editTextTeacherB);
        editRoomB = findViewById(R.id.editTextRoomB);

        checkBoxDouble = findViewById(R.id.checkBoxDouble);
        button = findViewById(R.id.buttonAdd);
        removeButton = findViewById(R.id.buttonRemove);

        init();
    }

    @Override
    public void onBackPressed() {
        Log.d("test", "back");
        Settings.save();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {

        spinnerA.setAdapter(new SpinnerAdapter());
        spinnerA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == spinnerA.getAdapter().getCount() - 1) {
                    spinnerA.setVisibility(View.GONE);
                    editSubjectA.setVisibility(View.VISIBLE);
                    editSubjectNameA.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerB.setAdapter(new SpinnerAdapter());
        spinnerB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == spinnerB.getAdapter().getCount() - 1) {
                    spinnerB.setVisibility(View.GONE);
                    editSubjectB.setVisibility(View.VISIBLE);
                    editSubjectNameB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(v -> {
            save();
            onBackPressed();
        });
        removeButton.setOnClickListener(v -> {
            TimeTable.TimeTableDay day = Settings.settings.myTimeTable.getDay(dayIndex);
            day.removeEntry(entryIndex);
            Settings.settings.myTimeTable.setDay(dayIndex, day);
            onBackPressed();
        });

        if (Settings.settings.myTimeTable.getDay(dayIndex).getSize() <= entryIndex || Settings.settings.myTimeTable.getDay(dayIndex).getEntry(entryIndex) == null) {
            return;
        }

        //Set Values
        TimeTable.TimeTableEntry entry = Settings.settings.myTimeTable.getDay(dayIndex).getEntry(entryIndex);

        if (entry.getEmptyA())
            spinnerA.setSelection(0);
        else
            spinnerA.setSelection(Settings.settings.symbols.getSymbolIndex(entry.getSubjectA()) + 1);
        editTeacherA.setText(entry.getTeacherA());
        editRoomA.setText(entry.getRoomA());

        if (entry.getEmptyB())
            spinnerB.setSelection(0);
        else
            spinnerB.setSelection(Settings.settings.symbols.getSymbolIndex(entry.getSubjectB()) + 1);
        editTeacherB.setText(entry.getTeacherB());
        editRoomB.setText(entry.getRoomB());
    }

    public void save() {
        String subjectA;
        if (editSubjectA.getVisibility() == View.VISIBLE) {
            subjectA = editSubjectA.getText().toString();
            Settings.settings.symbols.setSymbol(subjectA, editSubjectNameA.getText().toString());
        } else {
            subjectA = Settings.settings.symbols.getSymbol(spinnerA.getSelectedItemPosition() - 1);
        }

        String subjectB;
        if (editSubjectB.getVisibility() == View.VISIBLE) {
            subjectB = editSubjectB.getText().toString();
            Settings.settings.symbols.setSymbol(subjectB, editSubjectNameB.getText().toString());
        } else {
            subjectB = Settings.settings.symbols.getSymbol(spinnerB.getSelectedItemPosition() - 1);
        }

        TimeTable.TimeTableEntry entry = new TimeTable.TimeTableEntry(subjectA, editRoomA.getText().toString(), editTeacherA.getText().toString(), subjectB, editRoomB.getText().toString(), editTeacherB.getText().toString());
        entry.setEmptyA(spinnerA.getSelectedItemPosition() == 0);
        entry.setEmptyB(spinnerB.getSelectedItemPosition() == 0);
        TimeTable.TimeTableDay day = Settings.settings.myTimeTable.getDay(dayIndex);
        day.setEntry(entryIndex, entry);
        if(checkBoxDouble.isChecked())
            day.setEntry(entryIndex + 1, entry);
        Settings.settings.myTimeTable.setDay(dayIndex, day);
    }

    class SpinnerAdapter implements android.widget.SpinnerAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.symbol_entry_item, parent, false);
            TextView textView = view.findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.icon_color));

            int realPos = position - 1;

            if (position == 0) {
                view.findViewById(R.id.imageView).setVisibility(View.GONE);
                textView.setText(R.string.empty);
            } else if (position == getCount() - 1) {
                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_add_black_24dp);
                textView.setText(R.string.add);
            } else {
                view.findViewById(R.id.imageView).setVisibility(View.GONE);
                textView.setText(Settings.settings.symbols.getSymbolName(realPos));
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
            return Settings.settings.symbols.getCount() + 2;
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

            return getDropDownView(position, convertView, parent);
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