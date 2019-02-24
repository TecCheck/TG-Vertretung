package de.coop.tgvertretung;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Scanner;

public class LicenseActivity extends AppCompatActivity implements View.OnClickListener{

    public static int toShow = 0;
    TextView text = null;

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getStringArray(R.array.licenses)[toShow]);
        toolbar.addView(getButton());

        text = findViewById(R.id.license_content_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String fileName = "";
        url = getResources().getStringArray(R.array.licenses_urls)[toShow];

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

    ImageButton getButton(){
        ImageButton button = new ImageButton(getApplicationContext());
        button.setImageDrawable(getResources().getDrawable(R.drawable.ic_github));
        button.setElevation(0.3f);
        button.setBackgroundColor(0x00000000);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(100, 100,Gravity.END | Gravity.BOTTOM);
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