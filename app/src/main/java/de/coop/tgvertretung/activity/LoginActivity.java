package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.dsbmobile.DSBMobile;


public class LoginActivity extends AppCompatActivity {

    public static Button btn = null;
    public static EditText pwText = null;
    public static EditText nmText = null;
    public static boolean firstTime = true;

    public void login() {
        Boolean[] success = {false};
        Thread loginThread = new Thread(() -> {
            try {
                new DSBMobile(nmText.getText().toString(), pwText.getText().toString());
                success[0] = true;
            } catch (IllegalArgumentException e) {
                success[0] = false;
            } catch (Exception e) {
                e.printStackTrace();
                success[0] = null;
            }
        }, "Login-Thread");

        try {
            loginThread.start();
            loginThread.join();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (success[0] == null) {
            // phone is offline
            Snackbar.make(btn, getString(R.string.noConnection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (success[0]) {
            // credentials are correct
            Settings.load(getApplicationContext());
            Settings.settings.password = pwText.getText().toString();
            Settings.settings.username = nmText.getText().toString();
            Settings.settings.loggedIn = true;
            Settings.save();
            finish();
            //super.onBackPressed();
        } else {
            // credentials are incorrect
            Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

        Utils.print("ActionBar: " + actionBar);
    }

    @Override
    public void onBackPressed() {
        if (firstTime) {
            Utils.print("EXIT-------------------------------------------------------------------------");
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
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            //System.exit(1);
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