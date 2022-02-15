package de.coop.tgvertretung.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

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