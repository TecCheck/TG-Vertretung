package de.coop.tgvertretung.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.SubjectSymbols;

class ScheduleEntryAdapter extends RecyclerView.Adapter<ScheduleEntryAdapter.ViewHolder> {

    private final Context context;
    private final int dayOfWeek;
    private Schedule schedule;
    private SubjectSymbols symbols;

    public ScheduleEntryAdapter(int dayOfWeek, Context context, Schedule schedule, SubjectSymbols symbols) {
        this.dayOfWeek = dayOfWeek;
        this.context = context;
        this.schedule = schedule;
        this.symbols = symbols;
    }

    @Override
    public ScheduleEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_entry, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        cardView.setClickable(true);
        cardView.setFocusable(true);

        TextView schoolClass = cardView.findViewById(R.id.school_class);
        TextView hour = cardView.findViewById(R.id.hour);
        TextView entryText = cardView.findViewById(R.id.entry);
        TextView info = cardView.findViewById(R.id.info_text);
        ImageView imageView = cardView.findViewById(R.id.icon);


        if (position == schedule.getDaySize(ScheduleFragment.week, dayOfWeek)) {
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

        Schedule.ScheduleDayEntry entry = schedule.getEntry(ScheduleFragment.week, dayOfWeek, position);

        if (entry.subject == null || entry.subject.isEmpty()) {
            schoolClass.setText(R.string.item_empty);
            hour.setText("");

            entryText.setVisibility(View.GONE);
        } else {
            String subject = symbols.getSymbolName(entry.subject);
            schoolClass.setText(subject);

            hour.setText(entry.room);
            entryText.setText(entry.teacher);

            entryText.setVisibility(View.VISIBLE);
        }
        info.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        Log.d("getDaySize", "" + schedule.getDaySize(ScheduleFragment.week, dayOfWeek));
        return schedule.getDaySize(ScheduleFragment.week, dayOfWeek) + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;

        ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}