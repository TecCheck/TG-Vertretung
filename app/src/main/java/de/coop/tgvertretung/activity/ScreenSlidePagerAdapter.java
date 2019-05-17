package de.coop.tgvertretung.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.adapter.TableFragment;
import de.coop.tgvertretung.adapter.TableFragment2;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    public static boolean reset = false;

    ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(Settings.settings.useOldLayout){
            return TableFragment.newInstance(position);
        }
        return TableFragment2.newInstance(position);
    }

    @Override
    public int getCount() {
        return Settings.settings.timeTable.getTables().size();
    }

    @Override
    public int getItemPosition(Object object) {
        if(reset){
            return PagerAdapter.POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
