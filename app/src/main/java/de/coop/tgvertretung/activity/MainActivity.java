package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
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

    private static final boolean CLOSE_WARNING = false;
    private static boolean firstPagerStart = true;
    private ViewPager mPager = null;
    private TextView lastReload = null;
    private TextView lastServerRefresh = null;
    private SwipeRefreshLayout refreshLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.print("OnCreate-------------------------------------------------------------------------------");
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

        Utils.print("onCreate finished");
    }

    @Override
    protected void onResume() {
        Utils.print("OnResume-------------------------------------------------------------------------------");
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
        if(Settings.settings.themeMode == 0 && Build.VERSION.SDK_INT >= 29)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else
        AppCompatDelegate.setDefaultNightMode(Settings.settings.themeMode);

        // Get views
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

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

                        String date = null;
                        if (Settings.settings.relativeTime) date = Utils.getRelativeFormattedTime(Settings.settings.lastClientRefresh, new Date(System.currentTimeMillis()));
                        else date = Utils.getFormattedDate(Settings.settings.lastClientRefresh, false, true);
                        lastReload.setText(date);
                    } else {
                        lastReload.setVisibility(View.GONE);
                        title1.setVisibility(View.GONE);
                    }

                    if (Settings.settings.showServerRefresh) {
                        lastServerRefresh.setVisibility(View.VISIBLE);
                        title2.setVisibility(View.VISIBLE);

                        String date = null;
                        if (Settings.settings.relativeTime) date = Utils.getRelativeFormattedTime(Settings.settings.timeTable.getDate(), new Date(System.currentTimeMillis()));
                        else date = Utils.getFormattedDate(Settings.settings.timeTable.getDate(), false, true);
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
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.nav_drawer_text));
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.nav_drawer_icon));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        refreshLayout.setOnRefreshListener(this::load);
        try {
            refreshLayout.setColorSchemeColors(Utils.getColor(getApplicationContext(), Settings.settings.timeTable.getTables().get(0).getDate()));
        } catch (IndexOutOfBoundsException ignored) {}

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int i) {}

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
        Utils.print("SwipeRefreshLayout");
        refreshLayout.setRefreshing(true);

        if (!Downloader.download(this)) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void loadFinished(int status) {

        Utils.print("Status: " + status);

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


        //PagerAdapter adapter = mPager.getAdapter();
        //if (adapter != null) adapter.notifyDataSetChanged();
        startPagerView();

        Utils.print("Pager started!");

        if (firstPagerStart) {
            setTable(Utils.getView(Settings.settings.timeTable));
            firstPagerStart = false;
        } else {
            setTable(currentIndex);
        }

        Utils.print("Pager visible");
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        // If the back button is pressed
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Utils.print("EXIT-------------------------------------------------------------------------");
            if (CLOSE_WARNING) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.exit_title)
                        .setMessage(R.string.exit_message)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                            MainActivity.super.onBackPressed();
                            System.exit(1);
                        }).create();
                dialog.show();
            }

            super.onBackPressed();
            System.exit(1);
        }
    }

    private void setTable(int index) {
        if (index >= 0) mPager.setCurrentItem(index);
    }

    private void showSnack(String text) {
        Snackbar.make(mPager, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}