package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Scanner;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;

public class LicenseActivity extends AppCompatActivity implements View.OnClickListener {

    public static int toShow = 0;

    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getStringArray(R.array.licenses)[toShow]);
        toolbar.addView(getButton());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text = findViewById(R.id.license_content_text);

        String fileName = "";
        url = getResources().getStringArray(R.array.licenses_urls)[toShow];

        if (toShow == 0) fileName = "gson_license";
        else if (toShow == 1) fileName = "jsoup_license";
        else if (toShow == 2) fileName = "tg_api_license";

        InputStream stream = getResources().openRawResource(getResources().getIdentifier(fileName, "raw", getPackageName()));
        Scanner scanner = new Scanner(stream);

        Utils.print("File:");
        Utils.print(scanner.toString());

        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append('\n');
        }

        scanner.close();
        text.setText(content.toString());
    }

    private ImageButton getButton() {
        ImageButton button = new ImageButton(getApplicationContext());
        button.setImageDrawable(getResources().getDrawable(R.drawable.ic_github));
        button.setElevation(0.3f);
        button.setBackgroundColor(0x00000000);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(100, 100, Gravity.END | Gravity.BOTTOM);
        button.setLayoutParams(params);
        button.setOnClickListener(this);

        return button;
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
    public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}