package de.coop.tgvertretung.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.TimeTable;

class TimeTableEntryAdapter extends RecyclerView.Adapter<TimeTableEntryAdapter.ViewHolder> {
    private TimeTable.TimeTableDay day;
    private Context context;
    int dayOfWeek;

    TimeTableEntryAdapter(TimeTable.TimeTableDay day, int dayOfWeek, Context context) {
        this.day = day;
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
        TimeTable.TimeTableEntry entry = day.getEntry(position);
        CardView cardView = holder.cardView;

        TextView schoolClass = cardView.findViewById(R.id.school_class);
        TextView hour = cardView.findViewById(R.id.hour);
        TextView entryText = cardView.findViewById(R.id.entry);
        TextView info = cardView.findViewById(R.id.info_text);
        ImageView imageView = cardView.findViewById(R.id.imageView2);
        if(position == day.getSize()){
            imageView.setVisibility(View.VISIBLE);
            entryText.setText(R.string.add);
            hour.setVisibility(View.GONE);
            schoolClass.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            return;
        }

        schoolClass.setTextColor(context.getResources().getIntArray(R.array.day_of_week_color)[dayOfWeek]);
        hour.setTextColor(context.getResources().getIntArray(R.array.day_of_week_color)[dayOfWeek]);
        imageView.setVisibility(View.GONE);

        if(entry == null || entry.getEmptyA()){
            schoolClass.setText(R.string.item_empty);
            hour.setText("");
            entryText.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
        }else {
            String schoolCl = Settings.settings.symbols.getSymbolName(entry.getSubjectA());
            if(schoolCl == null){
                schoolCl = entry.getSubjectA();
            }
            schoolClass.setText(schoolCl);
            hour.setText(entry.getRoomA());
            entryText.setText(entry.getTeacherA());
            entryText.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return day.getSize() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}