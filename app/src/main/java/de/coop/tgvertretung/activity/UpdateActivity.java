package de.coop.tgvertretung.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Utils;


public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private String PATH = "";

    TextView updateStatus = null;
    Button updateButton = null;
    ProgressBar updateProgress = null;

    DownloadInfoTask downloadInfoTask = null;
    DownloadApkTask downloadApkTask = null;
    boolean updateAvailable = false;
    String updateUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            // show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        updateStatus = findViewById(R.id.update_status);
        updateButton = findViewById(R.id.update_button);
        updateProgress = findViewById(R.id.update_progress);

        updateButton.setOnClickListener(this);
        updateButton.setEnabled(false);

        PATH = Utils.getUpdateDownloadFile(this);

        searchForUpdate();
    }

    void searchForUpdate() {
        updateButton.setEnabled(false);
        updateStatus.setText(R.string.update_status_checking);
        updateProgress.setIndeterminate(true);
        downloadInfoTask = new DownloadInfoTask(getApplicationContext());
        downloadInfoTask.execute(getString(R.string.update_url));
    }

    public void searchForUpdateFinished(int status, String version, String link) {
        updateProgress.setIndeterminate(false);
        if (status == 0) {
            updateStatus.setText(R.string.update_status_failed);
            updateButton.setText(R.string.update_button_try_again);
            updateButton.setEnabled(true);
            updateAvailable = false;
            return;
        }

        updateUrl = link;
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

        Utils.print("Version: " + versionCode);
        Utils.print("Link: " + updateUrl);
    }

    void downloadUpdate() {
        updateButton.setEnabled(false);
        updateStatus.setText(R.string.update_status_downloading);
        downloadApkTask = new DownloadApkTask(getApplicationContext());
        downloadApkTask.execute(updateUrl);
    }

    public void downloadUpdateFinished(int status) {
        if(status == 0)
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(PATH)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
    public void onClick(View v) {
        if (v.getId() == updateButton.getId()) {
            if (updateAvailable) {
                downloadUpdate();
            } else {
                searchForUpdate();
            }
        }
    }

    private int getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    class DownloadInfoTask extends AsyncTask<String, Integer, String> {
        String version;
        String link;
        private Context context;

        public DownloadInfoTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                URL url = new URL(sUrl[0]);
                Scanner s = new Scanner(url.openStream());
                version = s.nextLine();
                link = s.nextLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            searchForUpdateFinished(1, version, link);
        }
    }

    class DownloadApkTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadApkTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(PATH);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire(12000);
            updateProgress.setIndeterminate(false);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            updateProgress.setIndeterminate(false);
            updateProgress.setMax(100);
            updateProgress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            downloadUpdateFinished(1);
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}