package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.Calendar;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TimeTableFragment;
import de.coop.tgvertretung.adapter.TimeTablePagerAdapter;
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.NewTimeTableSerializer;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Week;

public class TimeTableActivity extends AppCompatActivity {

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        if (TimeTableFragment.week == null) {
            TimeTableFragment.week = isAWeek() ? Week.A : Week.B;
        }

        mPager = findViewById(R.id.container);
        PagerAdapter mPagerAdapter = new TimeTablePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
        mPager.setCurrentItem((day >= 0 && day <= 4) ? day : 0);
    }

    @Override
    public void onResume() {
        //mPager.setAdapter(new TimeTablePagerAdapter(getSupportFragmentManager()));
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.share) {
            share();
        } else if (id == R.id.receive) {
            receive();
        } else if (id == R.id.test) {
            // reserved for testing
            //Settings.settings.myNewTimeTable.test();
        }

        return super.onOptionsItemSelected(item);
    }

    public void share() {
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_share_time_table);
        dialog.setTitle(R.string.share);
        dialog.setCancelable(true);
        Button ok = dialog.findViewById(R.id.button);
        ok.setOnClickListener(v -> dialog.dismiss());
        EditText editText = dialog.findViewById(R.id.editText);
        editText.setText(Settings.settings.myNewTimeTable.getJson().toString());
        dialog.show();
    }

    public void receive() {
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_receive_time_table);
        dialog.setTitle(R.string.receive);
        dialog.setCancelable(true);
        EditText editText = dialog.findViewById(R.id.editText);
        Button button = dialog.findViewById(R.id.button);
        Button cancel = dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(v -> dialog.dismiss());
        button.setOnClickListener(v -> {
            String s = editText.getText().toString();
            JsonParser parser = new JsonParser();
            try {
                JsonArray jsonArray = parser.parse(s).getAsJsonArray();
                NewTimeTable timeTable = NewTimeTableSerializer.getTimeTable(jsonArray);
                Settings.settings.myNewTimeTable = timeTable;
                PagerAdapter adapter = mPager.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            } catch (Exception e) {
                editText.setError(getString(R.string.wrong_json));
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_time_table_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Settings.save();
        super.onBackPressed();
    }

    private boolean isAWeek() {
        int i = Utils.getView(Settings.settings.timeTable, Settings.settings.timeTable.getTables().size() - 1);
        try {
            return Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("A") || Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("C");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return true;
        }
    }
}