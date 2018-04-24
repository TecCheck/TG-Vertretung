package de.coop.tgvertretung;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public class LoginActivity extends AppCompatActivity {
    private static final Gson gson = new Gson();
    public static SharedPreferences settings = null;
    public static Button btn = null;
    public static EditText pwText = null;
    public static EditText nmText = null;
    public static boolean firstTime = true;
    String text = "";
    String url = "";
    boolean fin = false;
    boolean offline = false;

    public void login() {

        try {
            String json = "[" + this.getStringFromURL("https://iphone.dsbcontrol.de/(S(bsiggfwxwakskze5ca4fd4ed))/iPhoneService.svc/DSB/authid/" + nmText.getText().toString() + "/" + pwText.getText().toString()) + "]";
            JsonArray jArray = gson.fromJson(json, JsonArray.class);
            String key = jArray.get(0).getAsString();

            if (key.equals("00000000-0000-0000-0000-000000000000")) {
                //wrong Password
                Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                //password is correct and phone is online
                SharedPreferences.Editor settingsEdit = settings.edit();
                settingsEdit.putBoolean(getString(R.string.settings_loginconfirmed), true);
                settingsEdit.putString(getString(R.string.settings_password), pwText.getText().toString());
                settingsEdit.putString(getString(R.string.settings_username), nmText.getText().toString());
                Client.singInConfirmed = true;
                Client.username = nmText.getText().toString();
                Client.password = pwText.getText().toString();
                settingsEdit.apply();
                if(firstTime)
                    Client.login = true;
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(btn, getString(R.string.noConnection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private String getStringFromURL(String url1) {
        url = url1;
        new Thread(new Dwd()).start();
        while (!fin) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pwText = (EditText) findViewById(R.id.password);
        nmText = (EditText) findViewById(R.id.username);
        btn = (Button) findViewById(R.id.sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        settings = getSharedPreferences(getString(R.string.settings_name), 0);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && firstTime) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        Client.print("ActionBar: " + actionBar);
    }

    @Override
    public void onBackPressed() {
        if (firstTime) {
            Client.print("EXIT-------------------------------------------------------------------------");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exitTitle)
                    .setMessage(R.string.exitMessage)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            System.exit(1);
                        }
                    }).create().show();
            System.exit(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!firstTime) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class Dwd implements Runnable {

        @Override
        public void run() {
            try {
                Scanner scanner;
                String line;
                for (scanner = new Scanner((new URL(url)).openStream()); scanner.hasNextLine(); text = text + line) {
                    for (line = scanner.nextLine(); line.startsWith(" ") || line.startsWith("\t"); line = line.substring(1)) {
                        ;
                    }
                }

                scanner.close();
            } catch (IOException var5) {
                var5.printStackTrace();
                offline = true;
            }
            fin = true;
        }

    }

}
