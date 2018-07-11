package de.coop.tgvertretung;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean payload = true;

    public static final boolean CLOSE_WARNING = false;
    public static MainActivity instance = null;
    public static ViewPager mPager;
    public LinearLayout mainLayout = null;
    public FloatingActionButton fab = null;
    public TextView lastReload = null;
    public TextView lastServerRefresh = null;
    public ConstraintLayout loadView = null;
    public LinearLayout stdView = null;
    public ProgressBar progBar = null;
    NavigationView navigationView = null;
    SharedPreferences settings = null;
    SharedPreferences table = null;
    private PagerAdapter mPagerAdapter;

    public MainActivity() {
        if (instance == null) {
            instance = this;
        }
    }

    public static void showSnack(String text) {
        Snackbar.make(instance.fab, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void startPagerView() {
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);

        mainLayout = (LinearLayout) findViewById(R.id.MainLayout);
    }

    public void setTable(int index) {
        if (index >= 0)
            mPager.setCurrentItem(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Client.print("OnCreate-------------------------------------------------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //the menu in the top left is created
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                lastReload = findViewById(R.id.lastReload);
                lastReload.setText(getString(R.string.lastReload) + " " + Client.lastReloadStr);

                lastServerRefresh = findViewById(R.id.lastReload2);
                if (Client.extendet) {
                    lastServerRefresh.setVisibility(View.VISIBLE);
                    lastServerRefresh.setText(getString(R.string.lastServerRefresh) + Client.lastserverRefreshStr);
                } else {
                    lastServerRefresh.setVisibility(View.GONE);
                }

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loadView = (ConstraintLayout) findViewById(R.id.loadView);
        stdView = (LinearLayout) findViewById(R.id.stdView);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mPager = (ViewPager) findViewById(R.id.container);
        navigationView.setNavigationItemSelectedListener(this);

        loadView.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.load(true);
            }
        });
        settings = MainActivity.instance.getSharedPreferences(getString(R.string.settings_name), 0);
        table = MainActivity.instance.getSharedPreferences(getString(R.string.tab_name), 0);

        new Client();

        Client.saveOfflineBool = instance.settings.getBoolean(getString(R.string.settings_saveofflinebool), Client.saveOfflineBool);
        Client.filter = instance.settings.getString(getString(R.string.settings_filter), Client.filter);
        //filter = settings.getInt("filter", filter);
        Client.extendet = instance.settings.getBoolean(getString(R.string.settings_extendet), Client.extendet);
        Client.useFilter = instance.settings.getBoolean(getString(R.string.settings_filterswitch), Client.useFilter);
        Client.password = instance.settings.getString(getString(R.string.settings_password), Client.password);
        Client.username = instance.settings.getString(getString(R.string.settings_username), Client.username);
        Client.singInConfirmed = settings.getBoolean(getString(R.string.settings_loginconfirmed), Client.singInConfirmed);

        if (Client.singInConfirmed /*&& !Client.password.equals("") && !Client.username.equals("")*/) {
            Client.load(true);
        } else {
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
        if (Client.login) {
            Client.load(true);
            Client.login = false;
        }
    }

    @Override
    public void onBackPressed() {
        //wenn der zurück Knopf gedrückt wird
        //if the back button is pressed
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Client.print("EXIT-------------------------------------------------------------------------");
            if (CLOSE_WARNING) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.exitTitle)
                        .setMessage(R.string.exitMessage)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                MainActivity.super.onBackPressed();
                                System.exit(1);
                            }
                        }).create().show();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}