package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.ScreenSlidePagerAdapter;
import de.coop.tgvertretung.service.BackgroundService;
import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Downloader.LoadFinishedListener {

    private static boolean firstPagerStart = true;
    private DrawerLayout drawer;
    private ViewPager mPager = null;
    private TextView lastReload = null;
    private TextView lastServerRefresh = null;
    private SwipeRefreshLayout refreshLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Settings.load(getApplicationContext());

        new File(Utils.getUpdateDownloadFile(this)).delete();
        Utils.print(Utils.getUpdateDownloadFile(this));

        initUi();
        startPagerView();

        // Load if Logged in
        if (Settings.settings.loggedIn) {
            load();
            if (!BackgroundService.isRunning) {
                startService(new Intent(getApplicationContext(), BackgroundService.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Settings.settings.loggedIn) {
            if (LoginActivity.recentLogin) {
                load();
                LoginActivity.recentLogin = false;
            }
            startPagerView();
            if (!BackgroundService.isRunning) {
                startService(new Intent(getApplicationContext(), BackgroundService.class));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUi() {
        if (Settings.settings.themeMode == 0 && Build.VERSION.SDK_INT >= 29)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else
            AppCompatDelegate.setDefaultNightMode(Settings.settings.themeMode);

        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        drawer = findViewById(R.id.drawer_layout);
        mPager = findViewById(R.id.container);
        refreshLayout = findViewById(R.id.refresh_layout);

        // Navigation Drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                lastReload = findViewById(R.id.last_reload);
                lastServerRefresh = findViewById(R.id.last_reload2);

                TextView title1 = findViewById(R.id.textView5);
                TextView title2 = findViewById(R.id.textView6);

                try {
                    if (Settings.settings.showClientRefresh) {
                        lastReload.setVisibility(View.VISIBLE);
                        title1.setVisibility(View.VISIBLE);

                        String date;
                        if (Settings.settings.relativeTime)
                            date = Utils.getRelativeFormattedTime(Settings.settings.lastClientRefresh, new Date(System.currentTimeMillis()));
                        else
                            date = Utils.getFormattedDate(Settings.settings.lastClientRefresh, false, true);
                        lastReload.setText(date);
                    } else {
                        lastReload.setVisibility(View.GONE);
                        title1.setVisibility(View.GONE);
                    }

                    if (Settings.settings.showServerRefresh) {
                        lastServerRefresh.setVisibility(View.VISIBLE);
                        title2.setVisibility(View.VISIBLE);

                        String date;
                        if (Settings.settings.relativeTime)
                            date = Utils.getRelativeFormattedTime(Settings.settings.timeTable.getDate(), new Date(System.currentTimeMillis()));
                        else
                            date = Utils.getFormattedDate(Settings.settings.timeTable.getDate(), false, true);
                        lastServerRefresh.setText(date);
                    } else {
                        lastServerRefresh.setVisibility(View.GONE);
                        title2.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lastReload.setText(getString(R.string.last_reload_none));
                    lastServerRefresh.setText(getString(R.string.last_reload_none));
                }

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setItemTextColor(AppCompatResources.getColorStateList(this, R.color.nav_drawer_text));
        navigationView.setItemIconTintList(AppCompatResources.getColorStateList(this, R.color.nav_drawer_icon));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        refreshLayout.setOnRefreshListener(this::load);
        try {
            refreshLayout.setColorSchemeColors(Utils.getColor(getApplicationContext(), Settings.settings.timeTable.getTables().get(0).getDate()));
        } catch (IndexOutOfBoundsException ignored) {
        }

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageSelected(int i) {
                refreshLayout.setColorSchemeColors(Utils.getColor(getApplicationContext(), Settings.settings.timeTable.getTables().get(i).getDate()));
            }
        });
    }

    private void startPagerView() {
        int index = Utils.getView(Settings.settings.timeTable);
        if (mPager != null) index = mPager.getCurrentItem();

        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);
        if (index >= 0) mPager.setCurrentItem(index);
    }

    private void load() {
        boolean success = Downloader.download(this);
        refreshLayout.setRefreshing(success);
    }

    @Override
    public void loadFinished(int status) {
        if (status != 1) Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
        Settings.save();

        refreshLayout.setRefreshing(false);

        if (status == 0) {
            showSnack(getString(R.string.connected));
        } else if (status == 1) {
            showSnack(getString(R.string.no_connection));
        } else if (status == 2) {
            showSnack(getString(R.string.nothing_new));
        }

        int currentIndex = mPager.getCurrentItem();
        startPagerView();

        if (firstPagerStart) {
            setTable(Utils.getView(Settings.settings.timeTable));
            firstPagerStart = false;
        } else {
            setTable(currentIndex);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_time_table) {
            startActivity(new Intent(this, TimeTableActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
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

    private void setTable(int index) {
        if (index >= 0) mPager.setCurrentItem(index);
    }

    private void showSnack(String text) {
        Snackbar.make(mPager, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}