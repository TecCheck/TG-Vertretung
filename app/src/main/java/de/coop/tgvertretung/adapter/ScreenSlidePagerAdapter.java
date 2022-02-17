package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.SettingsWrapper;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private final SettingsWrapper settings;
    private TimeTable timeTable;
    private SubjectSymbols symbols;

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, SettingsWrapper settings, TimeTable timeTable, SubjectSymbols symbols) {
        super(fragmentActivity);
        this.settings = settings;
        this.timeTable = timeTable;
        this.symbols = symbols;
    }

    @NonNull
    @Override
    public Fragment createFragment(int index) {
        return new TableFragment(index, settings, timeTable.getTables().get(index), symbols);
    }

    @Override
    public int getItemCount() {
        if (timeTable == null || symbols == null)
            return 0;

        return timeTable.getTables().size();
    }

    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
        notifyDataSetChanged();
    }

    public void setSymbols(SubjectSymbols symbols) {
        this.symbols = symbols;
        notifyDataSetChanged();
    }
}