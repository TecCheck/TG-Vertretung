package de.coop.tgvertretung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Christoph on 29.11.2017.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //System.out.println("tables size" + Client.tables.size());
        //System.out.println("posititon " + position);
        return new TableFragment().newInstance(position);
    }

    @Override
    public int getCount() {
        return Client.tables.size();
    }
}
