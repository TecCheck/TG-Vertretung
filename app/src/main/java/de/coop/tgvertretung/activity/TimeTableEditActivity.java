package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.Settings;
import de.sematre.tg.Week;

public class TimeTableEditActivity extends AppCompatActivity {

    Spinner spinnerA;
    EditText editSubjectA;
    EditText editSubjectNameA;
    EditText editTeacherA;
    EditText editRoomA;

    ConstraintLayout layoutB;
    ConstraintLayout dividerB;
    ImageView expander;

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

        layoutB = findViewById(R.id.layoutB);
        dividerB = findViewById(R.id.linearLayout5);
        expander = findViewById(R.id.imageView3);
        layoutB.setVisibility(View.GONE);
        dividerB.setOnClickListener(v -> {

            if(layoutB.getVisibility() == View.GONE) {
                layoutB.setVisibility(View.VISIBLE);
                expander.setRotation(180);
            }else {
                layoutB.setVisibility(View.GONE);
                expander.setRotation(0);
            }
        });

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
            Settings.settings.myNewTimeTable.removeEntry(Week.A, dayIndex, entryIndex);
            Settings.settings.myNewTimeTable.removeEntry(Week.B, dayIndex, entryIndex);
            onBackPressed();
        });

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
        // Set Values
        // New
        NewTimeTable.TimeTableDayEntry entryA = Settings.settings.myNewTimeTable.getEntry(Week.A, dayIndex, entryIndex);
        NewTimeTable.TimeTableDayEntry entryB = Settings.settings.myNewTimeTable.getEntry(Week.B, dayIndex, entryIndex);

        if(entryA == null) {
            checkBoxDouble.setChecked(true);
            return;
        }

        boolean emptyA = !NewTimeTable.notEmpty(entryA.subject);
        if(emptyA){
            spinnerA.setSelection(0);
        }else {
            spinnerA.setSelection(Settings.settings.symbols.getSymbolIndex(entryA.subject) + 1);
        }
        editRoomA.setText(entryA.room);
        editTeacherA.setText(entryA.teacher);

        if(entryB == null)
            return;

        boolean emptyB = !NewTimeTable.notEmpty(entryB.subject);
        if(emptyB){
            spinnerB.setSelection(0);
        }else {
            spinnerB.setSelection(Settings.settings.symbols.getSymbolIndex(entryB.subject) + 1);
        }
        editRoomB.setText(entryB.room);
        editTeacherB.setText(entryB.teacher);
    }

    public void save() {
        String subjectA;
        if (editSubjectA.getVisibility() == View.VISIBLE) {
            subjectA = editSubjectA.getText().toString();
            Settings.settings.symbols.setSymbol(subjectA, editSubjectNameA.getText().toString());
        } else if(spinnerA.getSelectedItemPosition() == 0){
            subjectA = "";
        } else {
            subjectA = Settings.settings.symbols.getSymbol(spinnerA.getSelectedItemPosition() - 1);
        }

        String subjectB;
        if (editSubjectB.getVisibility() == View.VISIBLE) {
            subjectB = editSubjectB.getText().toString();
            Settings.settings.symbols.setSymbol(subjectB, editSubjectNameB.getText().toString());
        } else if(spinnerB.getSelectedItemPosition() == 0){
            subjectB = "";
        } else {
            subjectB = Settings.settings.symbols.getSymbol(spinnerB.getSelectedItemPosition() - 1);
        }

        NewTimeTable.TimeTableDayEntry entryA = new NewTimeTable.TimeTableDayEntry();
        entryA.subject = subjectA;
        entryA.room = editRoomA.getText().toString();
        entryA.teacher = editTeacherA.getText().toString();

        NewTimeTable.TimeTableDayEntry entryB = new NewTimeTable.TimeTableDayEntry();

        if(layoutB.getVisibility() == View.GONE){
            entryB = entryA;
        }else {
            entryB.subject = subjectB;
            entryB.room = editRoomB.getText().toString();
            entryB.teacher = editTeacherB.getText().toString();
        }

        Settings.settings.myNewTimeTable.setEntry(Week.A, dayIndex, entryIndex, entryA);
        Settings.settings.myNewTimeTable.setEntry(Week.B, dayIndex, entryIndex, entryB);
        if(checkBoxDouble.isChecked()){
            Settings.settings.myNewTimeTable.setEntry(Week.A, dayIndex, entryIndex + 1, entryA);
            Settings.settings.myNewTimeTable.setEntry(Week.B, dayIndex, entryIndex + 1, entryB);
        }
    }

    class SpinnerAdapter implements android.widget.SpinnerAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.symbol_entry_item, parent, false);
            TextView textView = view.findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.primary_item_color));

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