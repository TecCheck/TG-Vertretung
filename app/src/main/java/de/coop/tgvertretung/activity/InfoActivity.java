package de.coop.tgvertretung.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import de.coop.tgvertretung.BuildConfig;
import de.coop.tgvertretung.R;

public class InfoActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Current version
        TextView versionNameTxt = findViewById(R.id.version_name_txt);
        versionNameTxt.setText(getVersion());

        // Set Link for GitHub website
        LinearLayout gitHub = findViewById(R.id.github);
        gitHub.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_url)));
            startActivity(browserIntent);
        });

        LinearLayout developers = findViewById(R.id.developers);
        developers.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.devs_url)));
            startActivity(browserIntent);
        });

        LinearLayout version = findViewById(R.id.version);
        version.setOnClickListener(v -> makeVersionDialog());

        LinearLayout licenses = findViewById(R.id.licenses);
        licenses.setOnClickListener(v -> makeLicenseDialog());
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(this, LicenseActivity.class);
        intent.putExtra(LicenseActivity.EXTRA_TO_SHOW, i);
        startActivity(intent);
    }

    private void makeLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.licenses_name);
        builder.setItems(R.array.licenses, this);
        builder.show();
    }

    private void makeVersionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_about_version);
        dialog.setTitle(R.string.version);
        TextView versionName = dialog.findViewById(R.id.version_name);
        TextView versionID = dialog.findViewById(R.id.version_id);
        TextView buildDate = dialog.findViewById(R.id.build_date);

        versionName.setText(getVersion());
        versionID.setText(getVersionID());
        buildDate.setText(getBuildDate());
        dialog.show();
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

    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getVersionID() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
            return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).getLongVersionCode());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBuildDate() {
        String s;
        DateFormat format = DateFormat.getDateInstance();
        s = format.format(new Date(BuildConfig.TIMESTAMP));
        return s;
    }
}