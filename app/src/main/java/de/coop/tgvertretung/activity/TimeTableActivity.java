package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.TimeTablePagerAdapter;
import de.coop.tgvertretung.utils.Settings;

public class TimeTableActivity extends AppCompatActivity {

    private ViewPager mPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        mPager = findViewById(R.id.container);
        PagerAdapter mPagerAdapter = new TimeTablePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
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
