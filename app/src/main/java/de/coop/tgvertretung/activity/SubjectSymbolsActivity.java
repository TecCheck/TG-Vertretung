package de.coop.tgvertretung.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.RecyclerItemClickListener;
import de.coop.tgvertretung.storage.DataManager;
import de.coop.tgvertretung.utils.TgvApp;
import de.coop.tgvertretung.utils.SubjectSymbols;

public class SubjectSymbolsActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    private AppCompatDialog dialog = null;
    private Button removeButton = null;
    private SubjectSymbolsAdapter adapter;

    private SubjectSymbols symbols = null;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_symbols);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        dataManager = ((TgvApp) getApplication()).getDataManager();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));
        adapter = new SubjectSymbolsAdapter();
        recyclerView.setAdapter(adapter);

        dataManager.getSubjectSymbols(this, false).observe(this, symbols -> {
            this.symbols = symbols;
            adapter.setSymbols(symbols);
        });

        dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_symbol_add);
        dialog.setTitle(R.string.add_symbol);
        dialog.setCancelable(true);
        removeButton = dialog.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(this::removeClick);
        Button addButton = dialog.findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(this::addClick);
    }

    private void addClick(View v) {
        EditText name = dialog.findViewById(R.id.editTextName);
        EditText symbol = dialog.findViewById(R.id.editTextSymbol);
        if (name.getText().toString().equals("") || symbol.getText().toString().equals("")) {
            Snackbar.make(v, R.string.please_add_text, Snackbar.LENGTH_SHORT).show();
        } else {
            symbols.setSymbol(symbol.getText().toString(), name.getText().toString());
            adapter.setSymbols(symbols);
            name.setText("");
            symbol.setText("");
            dialog.dismiss();
        }
    }

    private void removeClick(View v) {
        EditText name = dialog.findViewById(R.id.editTextName);
        EditText symbol = dialog.findViewById(R.id.editTextSymbol);
        if (name.getText().toString().isEmpty() || symbol.getText().toString().isEmpty()) {
            Snackbar.make(v, R.string.please_add_text, Snackbar.LENGTH_SHORT).show();
        } else {
            symbols.removeSymbol(symbol.getText().toString());
            adapter.setSymbols(symbols);
            name.setText("");
            symbol.setText("");
            dialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.share) {
            share();
        } else if (id == R.id.receive) {
            receive();
        }

        return super.onOptionsItemSelected(item);
    }

    public void share() {
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_share_time_table);
        dialog.setTitle(R.string.share);
        dialog.setCancelable(true);
        Button ok = dialog.findViewById(R.id.button);
        ok.setOnClickListener(v -> dialog.dismiss());
        EditText editText = dialog.findViewById(R.id.editText);
        editText.setText(symbols.getJson());
        dialog.show();
    }

    public void receive() {
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_receive_time_table);
        dialog.setTitle(R.string.receive);
        dialog.setCancelable(true);
        EditText editText = dialog.findViewById(R.id.editText);
        Button button = dialog.findViewById(R.id.button);
        Button cancel = dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(v -> dialog.dismiss());
        button.setOnClickListener(v -> {
            String s = editText.getText().toString();
            if (symbols.readJson(s)) {
                dialog.dismiss();
                dataManager.setSubjectSymbols(symbols);
            } else
                editText.setError(getString(R.string.wrong_json));
        });
        dialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (symbols.getCount() == position) {
            dialog.show();
            removeButton.setVisibility(View.GONE);
        } else {
            EditText name = dialog.findViewById(R.id.editTextName);
            EditText symbol = dialog.findViewById(R.id.editTextSymbol);
            name.setText(symbols.getSymbolName(position));
            symbol.setText(symbols.getSymbol(position));
            removeButton.setVisibility(View.VISIBLE);
            dialog.show();
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_time_table_menu, menu);
        return true;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    static class SubjectSymbolsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private SubjectSymbols symbols;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_symbol_entry, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            TextView textView = viewHolder.view.findViewById(R.id.textView);
            ImageView imageView = viewHolder.view.findViewById(R.id.imageView);

            if (i != getItemCount() - 1) {
                String symbol = symbols.getSymbol(i);
                String symbolName = symbols.getSymbolName(i);
                textView.setText(symbol + ": " + symbolName);
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                textView.setText(R.string.add);
            }
        }

        @Override
        public int getItemCount() {
            int count = symbols == null ? 0 : symbols.getCount();
            return count + 1;
        }

        public void setSymbols(SubjectSymbols symbols) {
            this.symbols = symbols;
            notifyDataSetChanged();
        }
    }
}