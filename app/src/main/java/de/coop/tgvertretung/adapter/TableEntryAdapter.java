package de.coop.tgvertretung.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;

class TableEntryAdapter extends RecyclerView.Adapter<TableEntryAdapter.ViewHolder> {
    private Table table;

    TableEntryAdapter(Table table) {
        this.table = table;
    }

    @Override
    public TableEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TableEntry entry = table.getTableEntries().get(position);
        CardView cardView = holder.cardView;

        TextView schoolClass = cardView.findViewById(R.id.school_class);
        TextView hour = cardView.findViewById(R.id.hour);
        TextView entryText = cardView.findViewById(R.id.entry);
        TextView info = cardView.findViewById(R.id.info_text);

        schoolClass.setText(entry.getSchoolClass());
        hour.setText(entry.getTime());
        entryText.setText(getEntryText(entry, Settings.settings.extended));
        if (Settings.settings.showText && !entry.getText().equals("")) {
            info.setVisibility(View.VISIBLE);
            info.setText(entry.getText());
        } else {
            info.setVisibility(View.GONE);
            info.setText("");
        }

        if (Settings.settings.rainbow) {
            Utils.addRainbow(schoolClass);
            Utils.addRainbow(hour);
        } else {
            int color = Utils.getColor(table.getDate());
            schoolClass.setTextColor(color);
            hour.setTextColor(color);
        }
    }

    private String getEntryText(TableEntry entry, boolean extended) {
        if (entry.getType().equals("Entfall") || entry.getReplacementRoom().equals("---") || entry.getReplacementSubject().equals("---")) {
            return entry.getSubject() + " " + Utils.mainActivity.getString(R.string.noClass);
        }

        return entry.getReplacementSubject() + " in " + entry.getReplacementRoom() + (extended ? (" statt " + entry.getSubject() + " in " + entry.getRoom()) : "");
    }

    @Override
    public int getItemCount() {
        return table.getTableEntries().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}