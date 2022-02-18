package de.coop.tgvertretung.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.coop.tgvertretung.R;

public class UpdateActivity extends AppCompatActivity {

    private static final String FILENAME = "update.apk";

    private TextView updateStatus = null;
    private Button updateButton = null;
    private ProgressBar updateProgress = null;

    private boolean updateAvailable = false;
    private String updateUrl = "";

    private ScheduledExecutorService executor;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        executor = Executors.newSingleThreadScheduledExecutor();
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        updateStatus = findViewById(R.id.update_status);
        updateButton = findViewById(R.id.update_button);
        updateProgress = findViewById(R.id.update_progress);

        updateButton.setEnabled(false);
        updateButton.setOnClickListener(view -> {
            if (updateAvailable)
                downloadUpdate();
            else
                searchForUpdate();
        });

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + FILENAME);
        if (file.exists())
            file.delete();

        searchForUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
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

    private void searchForUpdate() {
        updateButton.setEnabled(false);
        updateStatus.setText(R.string.update_status_checking);
        updateProgress.setIndeterminate(true);
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.schedule(() -> {
            try {
                URL url = new URL(getString(R.string.update_url));
                Scanner scanner = new Scanner(url.openStream());
                String version = scanner.nextLine();
                String updateUrl = scanner.nextLine();
                mainHandler.post(() -> searchForUpdateFinished(version, updateUrl));
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(this::searchForUpdateFinished);
            }
        }, 0, TimeUnit.NANOSECONDS);
    }

    private void searchForUpdateFinished() {
        updateProgress.setIndeterminate(false);
        updateStatus.setText(R.string.update_status_failed);
        updateButton.setText(R.string.update_button_try_again);
        updateButton.setEnabled(true);
        updateAvailable = false;
    }

    private void searchForUpdateFinished(String version, String updateUrl) {
        updateProgress.setIndeterminate(false);
        this.updateUrl = updateUrl;
        int versionCode = Integer.parseInt(version);
        int currentVersion = getAppVersion();

        if (versionCode > currentVersion) {
            updateAvailable = true;
            updateButton.setEnabled(true);
            updateStatus.setText(R.string.update_status_available);
        } else {
            updateAvailable = false;
            updateStatus.setText(R.string.update_status_newest);
            updateButton.setText(R.string.update_button_try_again);
            updateButton.setEnabled(true);
        }
    }

    private void downloadUpdate() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateUrl));
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, FILENAME);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(getString(R.string.download_title));
        request.setDescription(getString(R.string.download_description));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadManager.enqueue(request);
    }

    private int getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}