package de.coop.tgvertretung.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.RecyclerItemClickListener;
import de.coop.tgvertretung.utils.ClassSymbols;
import de.coop.tgvertretung.utils.Settings;

public class SubjectSymbolsActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    RecyclerView recyclerView = null;
    Dialog dialog = null;
    Button button = null;
    Button removeButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_symbols);
        recyclerView = findViewById(R.id.recyclerView);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Settings.settings.symbols == null) {
            Settings.settings.symbols = new ClassSymbols();
        }

        Adapter adapter = new Adapter();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getApplicationContext(), recyclerView, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_symbol_add);
        dialog.setTitle(R.string.add_symbol);
        dialog.setCancelable(true);
        button = dialog.findViewById(R.id.buttonAdd);
        removeButton = dialog.findViewById(R.id.buttonRemove);
        button.setOnClickListener(this);
        removeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            EditText name = dialog.findViewById(R.id.editTextName);
            EditText symbol = dialog.findViewById(R.id.editTextSymbol);
            if (name.getText().toString().equals("") || symbol.getText().toString().equals("")) {
                Snackbar.make(v, R.string.please_add_text, Snackbar.LENGTH_SHORT).show();
            } else {
                Settings.settings.symbols.setSymbol(symbol.getText().toString(), name.getText().toString());
                recyclerView.getAdapter().notifyDataSetChanged();
                name.setText("");
                symbol.setText("");
                dialog.dismiss();
            }
        } else if (v.equals(removeButton)) {
            EditText name = dialog.findViewById(R.id.editTextName);
            EditText symbol = dialog.findViewById(R.id.editTextSymbol);
            if (name.getText().toString().equals("") || symbol.getText().toString().equals("")) {
                Snackbar.make(v, R.string.please_add_text, Snackbar.LENGTH_SHORT).show();
            } else {
                Settings.settings.symbols.removeSymbol(symbol.getText().toString());
                recyclerView.getAdapter().notifyDataSetChanged();
                name.setText("");
                symbol.setText("");
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("test", "back");
        Settings.save();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (Settings.settings.symbols.getCount() == position) {
            dialog.show();
            removeButton.setVisibility(View.GONE);
        } else {
            EditText name = dialog.findViewById(R.id.editTextName);
            EditText symbol = dialog.findViewById(R.id.editTextSymbol);
            name.setText(Settings.settings.symbols.getSymbolName(position));
            symbol.setText(Settings.settings.symbols.getSymbol(position));
            removeButton.setVisibility(View.VISIBLE);
            dialog.show();
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.symbol_entry_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            TextView textView = viewHolder.view.findViewById(R.id.textView);
            ImageView imageView = viewHolder.view.findViewById(R.id.imageView);
            if (i != Settings.settings.symbols.getCount()) {
                textView.setText(Settings.settings.symbols.getSymbol(i) + ": " + Settings.settings.symbols.getSymbolName(i));
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                textView.setText(R.string.add);
            }
        }

        @Override
        public int getItemCount() {
            return Settings.settings.symbols.getCount() + 1;
        }
    }
}
