package de.coop.tgvertretung.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.SettingsWrapper;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    private static boolean reset = false;

    private final SettingsWrapper settings;

    public ScreenSlidePagerAdapter(FragmentManager fm, SettingsWrapper settings) {
        super(fm);
        this.settings = settings;
    }

    @Override
    public Fragment getItem(int position) {
        return new TableFragment(position, settings);
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