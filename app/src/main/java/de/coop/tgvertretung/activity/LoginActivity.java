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

    public static String EXTRA_RE_LOGIN = "re_login";

    private Button btn;
    private EditText pwText;
    private EditText nmText;
    private ProgressBar progressBar;
    private boolean reLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pwText = findViewById(R.id.password);
        nmText = findViewById(R.id.username);
        progressBar = findViewById(R.id.login_progress);

        btn = findViewById(R.id.sign_in_button);
        btn.setOnClickListener(view -> login());

        reLogin = getIntent().getBooleanExtra(EXTRA_RE_LOGIN, false);

        if (reLogin) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void login() {
        progressBar.setIndeterminate(true);
        Thread loginThread = new Thread(() -> {
            final String password = pwText.getText().toString();
            final String username = nmText.getText().toString();

            LoginResult result;
            try {
                new DSBMobile(username, password);
                result = LoginResult.SUCCESS;
            } catch (IllegalArgumentException e) {
                result = LoginResult.WRONG_CREDENTIALS;
            } catch (Exception e) {
                result = LoginResult.NO_CONNECTION;
            }

            final LoginResult res = result;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> loginFinished(res, username, password));
        }, "Login-Thread");
        loginThread.start();
    }

    void loginFinished(LoginResult result, String username, String password) {
        progressBar.setIndeterminate(false);

        if (result == LoginResult.SUCCESS) {
            SettingsWrapper.SettingsWriter writer = new SettingsWrapper.SettingsWriter(this);
            writer.setUsername(username);
            writer.setPassword(password);
            writer.writeEdits();
            goToMainActivity();
        } else if (result == LoginResult.WRONG_CREDENTIALS) {
            Snackbar.make(btn, getString(R.string.error_incorrect_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(btn, getString(R.string.no_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void goToMainActivity() {
        if (reLogin) {
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

    private enum LoginResult {
        SUCCESS,
        WRONG_CREDENTIALS,
        NO_CONNECTION
    }
}