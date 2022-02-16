package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.ScreenSlidePagerAdapter;
import de.coop.tgvertretung.service.BackgroundService;
import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.SettingsWrapper;
import de.coop.tgvertretung.utils.Utils;
import de.sematre.tg.Table;
import de.sematre.tg.TimeTable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Downloader.LoadFinishedListener {

    private ViewPager2 pager;
    private DrawerLayout drawer;
    private SwipeRefreshLayout refreshLayout;

    private SettingsWrapper settings;
    private SettingsWrapper.SettingsWriter settingsWriter;
    private Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new SettingsWrapper(this);
        if (!settings.isLoggedIn()) {
            startLoginActivity(false);
            return;
        }

        setContentView(R.layout.activity_main);

        Settings.load(this);
        settingsWriter = new SettingsWrapper.SettingsWriter(this);
        downloader = new Downloader(this, settings);

        refreshLayout = findViewById(R.id.refresh_layout);
        drawer = findViewById(R.id.drawer_layout);
        pager = findViewById(R.id.container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setNightMode(settings.getThemeMode());
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                TextView lastClientReload = findViewById(R.id.lastClientReload);
                TextView lastServerReload = findViewById(R.id.lastServerReload);

                try {
                    boolean relativeTime = settings.getShowRelativeTime();
                    showTime(lastClientReload, new Date(settings.getLastClientRefresh()), R.string.last_client_refresh, relativeTime, settings.getShowClientRefresh());
                    showTime(lastServerReload, Settings.settings.timeTable.getDate(), R.string.last_server_refresh, relativeTime, settings.getShowServerRefresh());
                } catch (Exception e) {
                    e.printStackTrace();
                    lastClientReload.setText(getString(R.string.last_reload_none));
                    lastServerReload.setText(getString(R.string.last_reload_none));
                }
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        pager.setAdapter(new ScreenSlidePagerAdapter(this, settings));
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int color = Utils.getColor(MainActivity.this, Settings.settings.timeTable.getTables().get(position).getDate());
                refreshLayout.setColorSchemeColors(color);
            }
        });

        setPage(Utils.getView(Settings.settings.timeTable));
        refreshLayout.setOnRefreshListener(this::load);

        load();
        startBackgroundService();
    }

    @Override
    protected void onDestroy() {
        Settings.save();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_time_table) {
            startActivity(new Intent(this, TimeTableActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_login) {
            startLoginActivity(true);
        } else if (id == R.id.nav_symbols) {
            startActivity(new Intent(this, SubjectSymbolsActivity.class));
        } else if (id == R.id.nav_update) {
            startActivity(new Intent(this, UpdateActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public void loadFinished(Downloader.DownloadResult result, TimeTable timeTable) {
        if (result != Downloader.DownloadResult.FAILED){
            settingsWriter.setLastClientRefresh(System.currentTimeMillis());
            settingsWriter.writeEditsAsync();
            Settings.settings.timeTable = timeTable;
        }

        refreshLayout.setRefreshing(false);

        if (result == Downloader.DownloadResult.SUCCESS) {
            showSnack(getString(R.string.connected));
            pager.getAdapter().notifyDataSetChanged();
        } else if (result == Downloader.DownloadResult.FAILED) {
            showSnack(getString(R.string.no_connection));
        } else if (result == Downloader.DownloadResult.NOTHING_NEW) {
            showSnack(getString(R.string.nothing_new));
        }
    }

    private void startLoginActivity(boolean reLogin) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_RE_LOGIN, reLogin);
        if (!reLogin)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setNightMode(int mode) {
        if (mode == 0 && Build.VERSION.SDK_INT >= 29)
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void showTime(TextView textView, Date date, int resId, boolean relativeTime, boolean visible) {
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible)
            return;

        String time;
        if (relativeTime)
            time = Utils.getRelativeFormattedTime(date, new Date(System.currentTimeMillis()));
        else
            time = Utils.getFormattedDate(date, false, true);

        textView.setText(getString(resId, time));
    }

    private void startBackgroundService() {
        // TODO: Better implementation
        if (!BackgroundService.isRunning(this)) {
            startService(new Intent(getApplicationContext(), BackgroundService.class));
        }
    }

    private void load() {
        refreshLayout.setRefreshing(downloader.download(Settings.settings.timeTable.getDate()));
    }

    private void setPage(int index) {
        ArrayList<Table> tables = Settings.settings.timeTable.getTables();
        if (0 > index || index >= tables.size())
            return;

        pager.setCurrentItem(index);
        refreshLayout.setColorSchemeColors(Utils.getColor(this, tables.get(index).getDate()));
    }

    private void showSnack(String text) {
        Snackbar.make(pager, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}