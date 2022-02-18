package de.coop.tgvertretung.activity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
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
import de.coop.tgvertretung.storage.DataManager;
import de.coop.tgvertretung.utils.TgvApp;
import de.coop.tgvertretung.utils.NewTimeTableSerializer;
import de.sematre.tg.Week;

public class TimeTableActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        dataManager = ((TgvApp) getApplication()).getDataManager();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        if (TimeTableFragment.week == null)
            TimeTableFragment.week = isAWeek() ? Week.A : Week.B;

        TimeTablePagerAdapter adapter = new TimeTablePagerAdapter(this);
        pager = findViewById(R.id.container);
        pager.setAdapter(adapter);

        dataManager.getNewTimeTable(this, false).observe(this, newTimeTable ->  {
            adapter.setNewTimeTable(newTimeTable);
            setDayIndex(adapter);
        });

        dataManager.getSubjectSymbols(this, false).observe(this, symbols -> {
            adapter.setSymbols(symbols);
            setDayIndex(adapter);
        });
    }

    private void setDayIndex(TimeTablePagerAdapter adapter) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
        trySetCurrentItem(adapter, (day >= 0 && day <= 4) ? day : 0);
    }

    private void trySetCurrentItem(TimeTablePagerAdapter adapter, int index) {
        if (adapter.getItemCount() > index)
            pager.setCurrentItem(index);
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
            Log.d(getLocalClassName(), "Test 123");
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
        dataManager.getNewTimeTable(this, false).observe(this, newTimeTable -> editText.setText(newTimeTable.getJson().toString()));

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
                dataManager.setNewTimeTable(NewTimeTableSerializer.getTimeTable(jsonArray));
                RecyclerView.Adapter adapter = pager.getAdapter();
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

    private boolean isAWeek() {
        return true;
        // TODO: Reimplement
        /*
        int i = Utils.getView(Settings.settings.timeTable, Settings.settings.timeTable.getTables().size() - 1);
        try {
            return Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("A") || Settings.settings.timeTable.getTables().get(i).getWeek().getLetter().equalsIgnoreCase("C");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return true;
        }
        */
    }
}