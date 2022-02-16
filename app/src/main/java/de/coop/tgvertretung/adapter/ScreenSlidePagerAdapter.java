package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.SettingsWrapper;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private final SettingsWrapper settings;

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, SettingsWrapper settings) {
        super(fragmentActivity);
        this.settings = settings;
    }

    @NonNull
    @Override
    public Fragment createFragment(int index) {
        return new TableFragment(index, settings);
    }

    @Override
    public int getItemCount() {
        return Settings.settings.timeTable.getTables().size();
    }
}