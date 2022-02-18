package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.Schedule;

public class SchedulePagerAdapter extends FragmentStateAdapter {

    private Schedule schedule;

    public SchedulePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;

        if (schedule != null)
            this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ScheduleFragment.create(position);
    }

    @Override
    public int getItemCount() {
        return schedule == null ? 0 : 5;
    }
}