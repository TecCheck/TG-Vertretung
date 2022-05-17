package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import de.coop.tgvertretung.adapter.ScheduleFragment;
import de.coop.tgvertretung.storage.DataManager;
import de.coop.tgvertretung.utils.TgvApp;
import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.ScheduleSerializer;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.Week;

public class ScheduleEditActivity extends AppCompatActivity {

    private Spinner spinnerA;
    private EditText editSubjectA;
    private EditText editSubjectNameA;
    private EditText editTeacherA;
    private EditText editRoomA;

    private ConstraintLayout layoutB;
    private ImageView expander;

    private Spinner spinnerB;
    private EditText editSubjectB;
    private EditText editSubjectNameB;
    private EditText editTeacherB;
    private EditText editRoomB;

    private CheckBox checkBoxDouble;

    private int entryIndex;
    private int dayIndex;
    private DataManager dataManager;

    private SubjectSymbols symbols;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        entryIndex = intent.getIntExtra(ScheduleFragment.ARG_ENTRY, 0);
        dayIndex = intent.getIntExtra(ScheduleFragment.ARG_INDEX, 0);
        dataManager = ((TgvApp) getApplication()).getDataManager();

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

        layoutB = findViewById(R.id.layoutB);
        expander = findViewById(R.id.imageView3);
        ConstraintLayout dividerB = findViewById(R.id.headerB);

        checkBoxDouble = findViewById(R.id.checkBoxDouble);
        Button addButton = findViewById(R.id.buttonAdd);
        Button removeButton = findViewById(R.id.buttonRemove);

        layoutB.setVisibility(View.GONE);
        dividerB.setOnClickListener(v -> {
            if (layoutB.getVisibility() == View.GONE) {
                layoutB.setVisibility(View.VISIBLE);
                expander.setRotation(180);
            } else {
                layoutB.setVisibility(View.GONE);
                expander.setRotation(0);
            }
        });

        addButton.setOnClickListener(v -> {
            save();
            onBackPressed();
        });

        removeButton.setOnClickListener(v -> {
            delete();
            onBackPressed();
        });

        dataManager.getSchedule(this, false).observe(this, newTimeTable -> {
            this.schedule = newTimeTable;
            tryInit();
        });

        dataManager.getSubjectSymbols(this, false).observe(this, symbols -> {
            this.symbols = symbols;
            tryInit();
        });
    }

    public void tryInit() {
        if (schedule != null && symbols != null)
            init();
    }

    public void init() {
        // Set Values
        // New
        Schedule.ScheduleDayEntry entryA = schedule.getEntry(Week.A, dayIndex, entryIndex);
        Schedule.ScheduleDayEntry entryB = schedule.getEntry(Week.B, dayIndex, entryIndex);

        if (entryA == null) {
            checkBoxDouble.setChecked(true);
            return;
        }

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

        boolean emptyA = !ScheduleSerializer.notEmpty(entryA.subject);
        spinnerA.setSelection(emptyA ? 0 : (symbols.getSymbolIndex(entryA.subject) + 1));

        editRoomA.setText(entryA.room);
        editTeacherA.setText(entryA.teacher);


        if (entryB == null) return;

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

        boolean emptyB = !ScheduleSerializer.notEmpty(entryB.subject);
        spinnerB.setSelection(emptyB ? 0 : (symbols.getSymbolIndex(entryB.subject) + 1));

        editRoomB.setText(entryB.room);
        editTeacherB.setText(entryB.teacher);
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

    public void delete() {
        schedule.removeEntry(Week.A, dayIndex, entryIndex);
        schedule.removeEntry(Week.B, dayIndex, entryIndex);
        dataManager.setSchedule(schedule);
    }

    public void save() {
        String subjectA;
        if (editSubjectA.getVisibility() == View.VISIBLE) {
            subjectA = editSubjectA.getText().toString();
            symbols.setSymbol(subjectA, editSubjectNameA.getText().toString());
        } else if (spinnerA.getSelectedItemPosition() == 0) {
            subjectA = "";
        } else {
            subjectA = symbols.getSymbol(spinnerA.getSelectedItemPosition() - 1);
        }

        String subjectB;
        if (editSubjectB.getVisibility() == View.VISIBLE) {
            subjectB = editSubjectB.getText().toString();
            symbols.setSymbol(subjectB, editSubjectNameB.getText().toString());
        } else if (spinnerB.getSelectedItemPosition() == 0) {
            subjectB = "";
        } else {
            subjectB = symbols.getSymbol(spinnerB.getSelectedItemPosition() - 1);
        }

        Schedule.ScheduleDayEntry entryA = schedule.getEntry(Week.A, dayIndex, entryIndex);
        entryA.subject = subjectA;
        entryA.room = editRoomA.getText().toString();
        entryA.teacher = editTeacherA.getText().toString();

        Schedule.ScheduleDayEntry entryB = schedule.getEntry(Week.A, dayIndex, entryIndex);
        if (layoutB.getVisibility() != View.GONE) {
            entryB.subject = subjectB;
            entryB.room = editRoomB.getText().toString();
            entryB.teacher = editTeacherB.getText().toString();
        }

        schedule.setEntry(Week.A, dayIndex, entryIndex, entryA);
        schedule.setEntry(Week.B, dayIndex, entryIndex, entryB);
        if (checkBoxDouble.isChecked()) {
            schedule.setEntry(Week.A, dayIndex, entryIndex + 1, entryA);
            schedule.setEntry(Week.B, dayIndex, entryIndex + 1, entryB);
        }

        dataManager.setSubjectSymbols(symbols);
        dataManager.setSchedule(schedule);
    }

    private class SpinnerAdapter implements android.widget.SpinnerAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_symbol_entry, parent, false);
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
                textView.setText(symbols.getSymbolName(realPos));
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
            return symbols.getCount() + 2;
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