package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Scanner;

import de.coop.tgvertretung.R;

public class LicenseActivity extends AppCompatActivity {

    public static final String EXTRA_TO_SHOW = "to_show";

    private static final int[] files = {R.raw.gson_license, R.raw.jsoup_license, R.raw.tg_api_license};

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        int toShow = getIntent().getIntExtra(EXTRA_TO_SHOW, 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView text = findViewById(R.id.license_content_text);

        toolbar.setTitle(getResources().getStringArray(R.array.licenses)[toShow]);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        url = getResources().getStringArray(R.array.licenses_urls)[toShow];
        text.setText(readFile(files[toShow]));
    }

    private String readFile(int fileId) {
        InputStream stream = getResources().openRawResource(fileId);
        Scanner scanner = new Scanner(stream);

        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append('\n');
        }

        scanner.close();
        return content.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.github) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_license_menu, menu);
        return true;
    }
}