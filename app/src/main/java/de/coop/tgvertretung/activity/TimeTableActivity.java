package de.coop.tgvertretung.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Calendar;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TimeTablePagerAdapter;
import de.coop.tgvertretung.utils.Settings;

public class TimeTableActivity extends AppCompatActivity {

    private ViewPager mPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPager = findViewById(R.id.container);
        PagerAdapter mPagerAdapter = new TimeTablePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day -= 2;

        if(day < 0 || day > 4){
            day = 0;
        }

        mPager.setCurrentItem(day);
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
        } else if(id == R.id.share){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_share_time_table);
            dialog.setTitle(R.string.share);
            dialog.setCancelable(true);
            EditText editText = dialog.findViewById(R.id.editText);
            editText.setText(Settings.settings.myNewTimeTable.getJson().toString());
            dialog.show();
        } else if(id == R.id.test){
            // reserved for testing
            //Settings.settings.myNewTimeTable.test();
        }

        return super.onOptionsItemSelected(item);
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
}