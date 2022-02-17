package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.sematre.tg.TimeTable;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private final SettingsWrapper settings;
    private TimeTable timeTable;

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, SettingsWrapper settings, TimeTable timeTable) {
        super(fragmentActivity);
        this.settings = settings;
        this.timeTable = timeTable;
    }

    @NonNull
    @Override
    public Fragment createFragment(int index) {
        return new TableFragment(index, settings, timeTable.getTables().get(index));
    }

    @Override
    public int getItemCount() {
        return timeTable.getTables().size();
    }
}