package de.coop.tgvertretung.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.adapter.ScreenSlidePagerAdapter;
import de.coop.tgvertretung.service.BackgroundService;
import de.coop.tgvertretung.utils.Download;
import de.coop.tgvertretung.utils.Settings;
import de.coop.tgvertretung.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Download.LoadFinishedListener {

    public static final boolean CLOSE_WARNING = false;
    private static boolean firstPagerStart = true;
    public ViewPager mPager = null;
    public LinearLayout mainLayout = null;
    public TextView lastReload = null;
    public TextView lastServerRefresh = null;
    public ConstraintLayout loadView = null;
    public LinearLayout stdView = null;
    public ProgressBar progBar = null;
    public SwipeRefreshLayout refreshLayout = null;

    Download download = null;

    public void showSnack(String text) {
        Snackbar.make(mPager, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void startPagerView() {
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);

        mainLayout = findViewById(R.id.MainLayout);
    }

    public void setTable(int index) {
        if (index >= 0) mPager.setCurrentItem(index);
    }

    public void load() {
        download = new Download(this);
        if (Settings.settings.useOldLayout) {
            stdView.setVisibility(View.INVISIBLE);
            loadView.setVisibility(View.VISIBLE);
            progBar.setEnabled(true);
        } else {
            Utils.print("SwipeRefreshLayout");
            refreshLayout.setRefreshing(true);
        }
        if (!download.download()) {
            refreshLayout.setRefreshing(false);
        }
    }

    void initUi() {
        setContentView(R.layout.activity_main);

        // Get views
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        loadView = findViewById(R.id.loadView);
        stdView = findViewById(R.id.stdView);
        progBar = findViewById(R.id.progressBar);
        mPager = findViewById(R.id.container);
        refreshLayout = findViewById(R.id.refresh_layout);

        // Navigation Drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                lastReload = findViewById(R.id.lastReload);
                lastServerRefresh = findViewById(R.id.lastReload2);

                Settings.print();
                if (Settings.settings.showClientRefresh) {
                    lastReload.setVisibility(View.VISIBLE);
                    String s = getString(R.string.lastReload) + " " + Utils.getFormattedDate(Settings.settings.lastClientRefresh, false, true);
                    lastReload.setText(s);
                } else {
                    lastReload.setVisibility(View.GONE);
                }

                if (Settings.settings.showServerRefresh) {
                    lastServerRefresh.setVisibility(View.VISIBLE);
                    String s = getString(R.string.lastServerRefresh) + Utils.getFormattedDate(Settings.settings.timeTable.getDate(), false, true);
                    lastServerRefresh.setText(s);
                } else {
                    lastServerRefresh.setVisibility(View.GONE);
                }

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        loadView.setVisibility(View.GONE);

        refreshLayout.setOnRefreshListener(() -> load());
        refreshLayout.setColorSchemeColors(Utils.getColor(getApplicationContext(), Settings.settings.timeTable.getTables().get(0).getDate()));
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

        startPagerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.print("OnCreate-------------------------------------------------------------------------------");
        super.onCreate(savedInstanceState);

        Settings.load(getApplicationContext());

        //Load if Logged in
        if (Settings.settings.loggedIn) {
            initUi();
            load();
            if (!BackgroundService.isRunning) {
                startService(new Intent(getApplicationContext(), BackgroundService.class));
            }
        } else {
            LoginActivity.firstTime = true;
            startActivity(new Intent(this, LoginActivity.class));
        }

        Utils.print("onCreate finished");
    }

    @Override
    protected void onResume() {
        Utils.print("OnResume-------------------------------------------------------------------------------");
        super.onResume();
        startPagerView();
    }

    @Override
    public void onBackPressed() {
        //if the back button is pressed
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Utils.print("EXIT-------------------------------------------------------------------------");
            if (CLOSE_WARNING) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.exitTitle)
                        .setMessage(R.string.exitMessage)
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_login) {
            LoginActivity.firstTime = false;
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void loadFinished(int status) {

        Utils.print("Status: " + status);

        Settings.settings.lastClientRefresh = new Date(System.currentTimeMillis());
        Settings.save();

        refreshLayout.setRefreshing(false);
        refreshLayout.setColorSchemeColors(Utils.getColor(getApplicationContext(), Settings.settings.timeTable.getTables().get(0).getDate()));

        if (status == 0) {
            showSnack(getString(R.string.connected));
        } else if (status == 1) {
            showSnack(getString(R.string.noConnection));
        } else if (status == 2) {
            showSnack(getString(R.string.nothingNew));
        }

        int i = mPager.getCurrentItem();

        startPagerView();
        Utils.print("Pager started!");

        if (firstPagerStart) {
            setTable(Utils.getView(Settings.settings.timeTable));
            firstPagerStart = false;
        } else {
            setTable(i);
        }

        loadView.setVisibility(View.GONE);
        progBar.setEnabled(false);
        stdView.setVisibility(View.VISIBLE);

        Utils.print("Pager visible");
    }
}