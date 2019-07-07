package de.coop.tgvertretung.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.coop.tgvertretung.R;


public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    TextView updateStatus = null;
    Button updateButton = null;
    ProgressBar updateProgress = null;

    DownloadTask downloadTask = null;
    UpdateThread updateThread = new UpdateThread();
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

        searchForUpdate();
    }

    void searchForUpdate() {
        if (updateThread != null && updateThread.isAlive()) {
            return;
        }
        updateButton.setEnabled(false);
        updateStatus.setText(R.string.update_status_checking);
        updateProgress.setIndeterminate(true);
        downloadTask = new DownloadTask(getApplicationContext(), 0);
        downloadTask.execute(getString(R.string.update_url));
    }

    void downloadUpdate() {
        if (updateThread != null && updateThread.isAlive()) {
            return;
        }
        updateButton.setEnabled(false);
        updateStatus.setText(R.string.update_status_downloading);
        downloadTask = new DownloadTask(getApplicationContext(), 1);
        downloadTask.execute(updateUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (updateThread != null && updateThread.isAlive()) {
                updateThread.interrupt();
            }
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

    public void downloadFinished(int type, String status) {
        if (type == 0) {
            updateProgress.setIndeterminate(false);
            if (status.equals("0")) {
                updateStatus.setText(R.string.update_status_failed);
                updateButton.setText(R.string.update_button_try_again);
                updateButton.setEnabled(true);
                updateAvailable = false;
                return;
            }

            updateUrl = status.substring(status.indexOf("\n") + 1);
            int versionCode = Integer.parseInt(status.substring(0, status.indexOf("\n")));
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

        } else {
            //Install
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

    class UpdateThread extends Thread {
        String downloadUrl;
        int type;
        UpdateActivity activity;

        public void init(String downloadUrl, int type, UpdateActivity activity) {
            this.downloadUrl = downloadUrl;
            this.type = type;
            this.activity = activity;
        }

        @Override
        public void run() {
            String s = "237\n" +
                    "https://github.com/TecCheck/TG-Vertretung/releases/download/Beta1.10.4/TG-Vertretung-Beta1.10.4.apk";

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                if (activity != null)
                    activity.downloadFinished(type, s);
            });
        }
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {

        int type;
        String out;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context, int type) {
            this.type = type;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            if (type == 0) {
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
                    output = new FileOutputStream("/sdcard/TGV.apk");

                    byte data[] = new byte[fileLength];
                    input.read(data);
                    out = new String(data);
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

            } else {
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
                    output = new FileOutputStream("/sdcard/TGV.apk");

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
            downloadFinished(type, out);
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
