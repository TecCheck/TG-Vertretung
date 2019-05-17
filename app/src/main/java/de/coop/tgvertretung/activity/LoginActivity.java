package de.coop.tgvertretung.activity;

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

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.R;
import de.coop.tgvertretung.Settings;
import de.sematre.dsbmobile.DSBMobile;


public class LoginActivity extends AppCompatActivity {

    public static Button btn = null;
    public static EditText pwText = null;
    public static EditText nmText = null;
    public static boolean firstTime = true;

    public void login() {
        try {
            new DSBMobile(nmText.getText().toString(), pwText.getText().toString());

            // credentials are correct
            Settings.settings.password = pwText.getText().toString();
            Settings.settings.username = nmText.getText().toString();
            Settings.settings.loggedIn = true;
            Settings.save();
            super.onBackPressed();
        } catch (IllegalArgumentException e) {
            // credentials are incorrect
            Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (Exception e) {
            // phone is offline
            e.printStackTrace();
            Snackbar.make(btn, getString(R.string.noConnection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pwText = findViewById(R.id.password);
        nmText = findViewById(R.id.username);

        btn = findViewById(R.id.sign_in_button);
        btn.setOnClickListener((view) -> login());

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
            /*
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exitTitle)
                    .setMessage(R.string.exitMessage)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            System.exit(1);
                        }
                    }).create().show();
                    */
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
}