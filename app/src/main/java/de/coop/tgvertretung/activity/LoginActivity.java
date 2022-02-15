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
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.sematre.dsbmobile.DSBMobile;

public class LoginActivity extends AppCompatActivity {

    public static String EXTRA_RELOGIN = "relogin";

    private Button btn;
    private EditText pwText;
    private EditText nmText;
    private ProgressBar progressBar;
    private SettingsWrapper settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new SettingsWrapper(this);

        if (settings.isLoggedIn() && !getIntent().getBooleanExtra(EXTRA_RELOGIN, false)) {
            startMainActivity();
        }

        setContentView(R.layout.activity_login);

        pwText = findViewById(R.id.password);
        nmText = findViewById(R.id.username);
        progressBar = findViewById(R.id.login_progress);

        btn = findViewById(R.id.sign_in_button);
        btn.setOnClickListener((view) -> login());

        if (settings.isLoggedIn()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void login() {
        progressBar.setIndeterminate(true);
        Thread loginThread = new Thread(() -> {
            final String password = pwText.getText().toString();
            final String username = nmText.getText().toString();

            int status;
            try {
                new DSBMobile(username, password);
                status = 0;
            } catch (IllegalArgumentException e) {
                status = 1;
            } catch (Exception e) {
                status = 2;
            }

            final int i = status;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> loginFinished(i, username, password));
        }, "Login-Thread");
        loginThread.start();
    }

    void loginFinished(int status, String username, String password) {
        progressBar.setIndeterminate(false);
        if (status == 0) {
            SettingsWrapper.SettingsWriter writer = new SettingsWrapper.SettingsWriter(this);
            writer.setUsername(username);
            writer.setPassword(password);
            writer.writeEdits();
            goToMainActivity();
        } else if (status == 1) {
            // Credentials are incorrect
            Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            // Phone is offline
            Snackbar.make(btn, getString(R.string.no_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void goToMainActivity() {
        if (getIntent().getBooleanExtra(EXTRA_RELOGIN, false)) {
            finish();
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}