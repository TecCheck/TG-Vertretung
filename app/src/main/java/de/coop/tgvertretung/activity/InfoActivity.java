package de.coop.tgvertretung.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;

public class InfoActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // current version
        TextView versionNameTxt = findViewById(R.id.version_name_txt);
        versionNameTxt.setText(getVersion());

        //set Link for GitHub website
        LinearLayout gitHub = findViewById(R.id.github);
        gitHub.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_url)));
            startActivity(browserIntent);
        });

        LinearLayout licenses = findViewById(R.id.licenses);
        licenses.setOnClickListener(v -> makeLicenseDialog());
    }

    private void makeLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.licenses_name);
        builder.setItems(R.array.licenses, this);
        builder.show();
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
    public void onClick(DialogInterface dialogInterface, int i) {
        LicenseActivity.toShow = i;
        startActivity(new Intent(this, LicenseActivity.class));
    }

    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}