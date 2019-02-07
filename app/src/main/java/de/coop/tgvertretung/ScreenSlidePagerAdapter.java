package de.coop.tgvertretung;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new TableFragment().newInstance(position);
    }

    @Override
    public int getCount() {
        return Settings.settings.timeTable.getTables().size();
    }
}
