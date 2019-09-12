package de.coop.tgvertretung.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.ClassSymbols;
import de.coop.tgvertretung.utils.Settings;

public class ClassSymbolsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView = null;
    FloatingActionButton floatingActionButton = null;
    Dialog dialog = null;
    ImageButton imageButtonAdd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_symbols);
        recyclerView = findViewById(R.id.recyclerView);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (Settings.settings.symbols == null) {
            Settings.settings.symbols = new ClassSymbols();
        }

        Adapter adapter = new Adapter();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.symbol_add_dialog);
        dialog.setTitle(R.string.add_symbol);
        imageButtonAdd = dialog.findViewById(R.id.imageButtonAdd);
        imageButtonAdd.setOnClickListener(this);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(imageButtonAdd)) {
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
        } else if (v.equals(floatingActionButton)) {
            dialog.show();
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
            textView.setText(Settings.settings.symbols.getSymbol(i) + ": " + Settings.settings.symbols.getSymbolName(i));
        }

        @Override
        public int getItemCount() {
            return Settings.settings.symbols.getCount();
        }
    }
}
