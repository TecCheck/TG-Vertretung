package de.coop.tgvertretung.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import de.coop.tgvertretung.utils.Settings;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    private static boolean reset = false;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TableFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return Settings.settings.timeTable.getTables().size();
    }

    @Override
    public int getItemPosition(Object object) {
        return reset ? PagerAdapter.POSITION_NONE : super.getItemPosition(object);
    }
}