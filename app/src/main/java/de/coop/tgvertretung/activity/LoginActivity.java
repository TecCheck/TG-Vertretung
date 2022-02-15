package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.dsbmobile.DSBMobile;


public class LoginActivity extends AppCompatActivity {

    public static boolean recentLogin = false;

    private Button btn = null;
    private EditText pwText = null;
    private EditText nmText = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pwText = findViewById(R.id.password);
        nmText = findViewById(R.id.username);
        progressBar = findViewById(R.id.login_progress);

        btn = findViewById(R.id.sign_in_button);
        btn.setOnClickListener((view) -> login());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(Settings.settings.loggedIn);

        Utils.print("ActionBar: " + actionBar);
    }

    private void login() {
        progressBar.setIndeterminate(true);
        Thread loginThread = new Thread(() -> {
            int status;
            try {
                new DSBMobile(nmText.getText().toString(), pwText.getText().toString());
                status = 0;
            } catch (IllegalArgumentException e) {
                status = 1;
            } catch (Exception e) {
                e.printStackTrace();
                status = 2;
            }

            final int i = status;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> loginFinished(i));
        }, "Login-Thread");
        loginThread.start();
    }

    void loginFinished(int status) {
        progressBar.setIndeterminate(false);
        if (status == 0) {
            Settings.load(getApplicationContext());
            Settings.settings.password = pwText.getText().toString();
            Settings.settings.username = nmText.getText().toString();
            Settings.settings.loggedIn = true;
            Settings.save();
            recentLogin = true;
            finish();
            //super.onBackPressed();
        } else if (status == 1) {
            // Credentials are incorrect
            Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            // Phone is offline
            Snackbar.make(btn, getString(R.string.no_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Settings.settings.loggedIn) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                super.onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!Settings.settings.loggedIn) {
            Utils.print("EXIT-------------------------------------------------------------------------");
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            //System.exit(1);
        } else {
            super.onBackPressed();
        }
    }
}