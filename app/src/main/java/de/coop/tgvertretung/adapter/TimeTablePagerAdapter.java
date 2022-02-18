package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.NewTimeTable;

public class TimeTablePagerAdapter extends FragmentStateAdapter {

    private NewTimeTable newTimeTable;

    public TimeTablePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setNewTimeTable(NewTimeTable newTimeTable) {
        this.newTimeTable = newTimeTable;

        if (newTimeTable != null)
            this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TimeTableFragment.create(position);
    }

    @Override
    public int getItemCount() {
        return newTimeTable == null ? 0 : 5;
    }
}