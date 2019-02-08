package de.coop.tgvertretung;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Scanner;

public class LicenseActivity extends AppCompatActivity {

    public static int toShow = 0;
    TextView text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getStringArray(R.array.licenses)[toShow]);
        text = findViewById(R.id.license_content_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String fileName = "";

        if(toShow == 0)
            fileName = "gson_license";
        else if(toShow == 1)
            fileName = "jsoup_license";
        else if(toShow == 2)
            fileName = "tg_api_license";

        InputStream ins = getResources().openRawResource(getResources().getIdentifier(fileName, "raw", getPackageName()));
        Scanner scan = new Scanner(ins);

        Client.print("File:");
        Client.print(scan.toString());

        String file = "";
        while (scan.hasNext()){
            file = file + "\n" + scan.nextLine();
        }
        text.setText(file);
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
}