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
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;

class TimeTableEntryAdapter extends RecyclerView.Adapter<TimeTableEntryAdapter.ViewHolder> {

    private final Context context;
    private final int dayOfWeek;
    private NewTimeTable newTimeTable;
    private SubjectSymbols symbols;

    public TimeTableEntryAdapter(int dayOfWeek, Context context, NewTimeTable newTimeTable, SubjectSymbols symbols) {
        this.dayOfWeek = dayOfWeek;
        this.context = context;
        this.newTimeTable = newTimeTable;
        this.symbols = symbols;
    }

    @Override
    public TimeTableEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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


        if (position == newTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek)) {
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

        NewTimeTable.TimeTableDayEntry entry = newTimeTable.getEntry(TimeTableFragment.week, dayOfWeek, position);

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
        Log.d("getDaySize", "" + newTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek));
        return newTimeTable.getDaySize(TimeTableFragment.week, dayOfWeek) + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;

        ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}