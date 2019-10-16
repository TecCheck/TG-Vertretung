package de.coop.tgvertretung.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.Settings;
import de.sematre.tg.Week;

class TimeTableEntryAdapter extends RecyclerView.Adapter<TimeTableEntryAdapter.ViewHolder> {
    private Context context;
    private int dayOfWeek;

    TimeTableEntryAdapter(int dayOfWeek, Context context) {
        this.dayOfWeek = dayOfWeek;
        this.context = context;
    }

    @Override
    public TimeTableEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.table_entry_item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        TextView schoolClass = cardView.findViewById(R.id.school_class);
        TextView hour = cardView.findViewById(R.id.hour);
        TextView entryText = cardView.findViewById(R.id.entry);
        TextView info = cardView.findViewById(R.id.info_text);
        ImageView imageView = cardView.findViewById(R.id.imageView2);
        if(position == Settings.settings.myNewTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek)){
            imageView.setVisibility(View.VISIBLE);
            entryText.setVisibility(View.VISIBLE);
            entryText.setText(R.string.add);
            hour.setVisibility(View.GONE);
            schoolClass.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            return;
        }

        schoolClass.setTextColor(context.getResources().getIntArray(R.array.day_of_week_color)[dayOfWeek]);
        hour.setTextColor(context.getResources().getIntArray(R.array.day_of_week_color)[dayOfWeek]);

        imageView.setVisibility(View.GONE);
        entryText.setVisibility(View.GONE);
        hour.setVisibility(View.VISIBLE);
        schoolClass.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);

        NewTimeTable.TimeTableDayEntry entry = Settings.settings.myNewTimeTable.getEntry(TimeTableFragment.week, dayOfWeek, position);

        if(entry.subject == null || entry.subject.isEmpty()){
            schoolClass.setText(R.string.item_empty);
            hour.setText("");
            entryText.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
        }else {
            String subject = Settings.settings.symbols.getSymbolName(entry.subject);
            schoolClass.setText(subject);
            hour.setText(entry.room);
            entryText.setText(entry.teacher);
            entryText.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        Log.wtf("getDaySize", "" + Settings.settings.myNewTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek));
        return Settings.settings.myNewTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek) + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}