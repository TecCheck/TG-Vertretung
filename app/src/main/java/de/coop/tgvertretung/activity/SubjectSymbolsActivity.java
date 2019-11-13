package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.coop.tgvertretung.utils.Settings;

public class SubjectSymbolsActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    private RecyclerView recyclerView = null;
    private AppCompatDialog dialog = null;
    private Button button = null;
    private Button removeButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_symbols);
        recyclerView = findViewById(R.id.recyclerView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        if (Settings.settings.symbols == null) {
            Settings.settings.symbols = new SubjectSymbols();
        }

        Adapter adapter = new Adapter();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getApplicationContext(), recyclerView, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        
        dialog = new AppCompatDialog(this);
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
            if (name.getText().toString().isEmpty() || symbol.getText().toString().isEmpty()) {
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
        } else if (id == R.id.share) {
            share();
        } else if(id == R.id.receive) {
            receive();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_time_table_menu, menu);
        return true;
    }

    @Override
    public void onLongItemClick(View view, int position) {}

    public void share(){
        AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.dialog_share_time_table);
        dialog.setTitle(R.string.share);
        dialog.setCancelable(true);
        Button ok = dialog.findViewById(R.id.button);
        ok.setOnClickListener(v -> dialog.dismiss());
        EditText editText = dialog.findViewById(R.id.editText);
        editText.setText(Settings.settings.symbols.getJson());
        dialog.show();
    }

    public void receive(){
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
            if(Settings.settings.symbols.readJson(s))
                dialog.dismiss();
            else
                editText.setError(getString(R.string.wrong_json));
        });
        dialog.show();
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