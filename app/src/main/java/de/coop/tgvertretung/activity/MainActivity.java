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

import de.coop.tgvertretung.Client;
import de.coop.tgvertretung.R;
import de.coop.tgvertretung.Settings;
import de.coop.tgvertretung.service.BackgroundService;
import de.coop.tgvertretung.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final boolean CLOSE_WARNING = false;

    public ViewPager mPager = null;
    public LinearLayout mainLayout = null;
    public TextView lastReload = null;
    public TextView lastServerRefresh = null;
    public ConstraintLayout loadView = null;
    public LinearLayout stdView = null;
    public ProgressBar progBar = null;
    public SwipeRefreshLayout refreshLayout = null;

    private NavigationView navigationView = null;
    private PagerAdapter mPagerAdapter = null;

    public static void showSnack(String text) {
        Snackbar.make(Utils.mainActivity.mPager, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void startPagerView() {
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);

        mainLayout = findViewById(R.id.MainLayout);
    }

    public void setTable(int index) {
        if (index >= 0) mPager.setCurrentItem(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.mainActivity = this;
        Client.print("OnCreate-------------------------------------------------------------------------------");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Settings.load();

        // the menu in the top left is created
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                lastReload = findViewById(R.id.lastReload);
                lastServerRefresh = findViewById(R.id.lastReload2);

                Settings.print();
                if (Settings.settings.showClientRefresh) {
                    lastReload.setVisibility(View.VISIBLE);
                    String s = getString(R.string.lastReload) + " " + Client.getFormattedDate(Settings.settings.lastClientRefresh, false, true);
                    lastReload.setText(s);
                } else {
                    lastReload.setVisibility(View.GONE);
                }

                if (Settings.settings.showServerRefresh) {
                    lastServerRefresh.setVisibility(View.VISIBLE);
                    String s = getString(R.string.lastServerRefresh) + Client.getFormattedDate(Settings.settings.timeTable.getDate(), false, true);
                    lastServerRefresh.setText(s);
                } else {
                    lastServerRefresh.setVisibility(View.GONE);
                }

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        loadView = findViewById(R.id.loadView);
        stdView = findViewById(R.id.stdView);
        progBar = findViewById(R.id.progressBar);
        mPager = findViewById(R.id.container);
        refreshLayout = findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(() -> Client.load(true));
        refreshLayout.setColorSchemeColors(Utils.getColor(Settings.settings.timeTable.getTables().get(0).getDate()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageSelected(int i) {
                refreshLayout.setColorSchemeColors(Utils.getColor(Settings.settings.timeTable.getTables().get(i).getDate()));
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        loadView.setVisibility(View.GONE);

        new Client();
        if (Settings.settings.loggedIn) {
            startPagerView();
            Client.load(true);
            if (!BackgroundService.isRunning) {
                startService(new Intent(getApplicationContext(), BackgroundService.class));
            }
        } else {
            LoginActivity.firstTime = true;
            startActivity(new Intent(this, LoginActivity.class));
        }

        Client.print("onCreate finished");
    }

    @Override
    protected void onResume() {
        Client.print("OnResume-------------------------------------------------------------------------------");
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);

        Client.print("starting Pager");
        int i = mPager.getCurrentItem();
        startPagerView();
        setTable(i);
        if (Settings.settings.loggedIn) {
            Client.load(true);
        }
    }

    @Override
    public void onBackPressed() {
        //if the back button is pressed
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Client.print("EXIT-------------------------------------------------------------------------");
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
}