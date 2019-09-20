package de.coop.tgvertretung.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import de.coop.tgvertretung.utils.Settings;

public class TimeTablePagerAdapter extends FragmentPagerAdapter {


    public TimeTablePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TimeTableFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public void update(){
        for(int i = 0; i < getCount(); i++){
            TimeTableFragment fragment = (TimeTableFragment) this.getItem(i);
            fragment.update();
        }
    }
}