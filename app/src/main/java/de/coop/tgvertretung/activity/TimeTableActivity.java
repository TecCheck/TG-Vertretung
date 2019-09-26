package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TimeTableFragment;
import de.coop.tgvertretung.adapter.TimeTablePagerAdapter;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Settings.save();
        super.onBackPressed();
    }
}