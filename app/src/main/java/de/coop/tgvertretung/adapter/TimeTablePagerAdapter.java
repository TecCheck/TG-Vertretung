package de.coop.tgvertretung.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
}