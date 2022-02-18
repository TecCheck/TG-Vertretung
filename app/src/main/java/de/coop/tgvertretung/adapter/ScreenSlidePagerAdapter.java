package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.sematre.tg.TimeTable;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private TimeTable timeTable;

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int index) {
        return TableFragment.create(index);
    }

    @Override
    public int getItemCount() {
        if (timeTable == null)
            return 0;

        return timeTable.getTables().size();
    }

    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
        notifyDataSetChanged();
    }
}