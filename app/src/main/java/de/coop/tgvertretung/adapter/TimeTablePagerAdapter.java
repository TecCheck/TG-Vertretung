package de.coop.tgvertretung.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;

public class TimeTablePagerAdapter extends FragmentStateAdapter {

    private SubjectSymbols symbols;
    private NewTimeTable newTimeTable;

    public TimeTablePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setNewTimeTable(NewTimeTable newTimeTable) {
        this.newTimeTable = newTimeTable;

        if (symbols != null && newTimeTable != null)
            this.notifyDataSetChanged();
    }

    public void setSymbols(SubjectSymbols symbols) {
        this.symbols = symbols;

        if (symbols != null && newTimeTable != null)
            this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TimeTableFragment(newTimeTable, symbols, position);
    }

    @Override
    public int getItemCount() {
        return symbols == null || newTimeTable == null ? 0 : 5;
    }
}