package de.coop.tgvertretung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final String PREFS_NAME = "Settings";
    public static final String FILTER_KEY = "filter";
    public static final String SAVE_KEY = "saveOfflineBool";
    public static final String EXTENDET_KEY = "extendet";
    public static final String FILTER_SWITCH_KEY = "filterSwitch";
    SharedPreferences settings = null;
    Preference filter = null;
    Preference saveOffline = null;
    Preference extendetView = null;
    Preference filterSwitch = null;
    String filterString = "";
    //int filterInt = -1;
    boolean saveOfflineBool = true;
    boolean extendetViewBool = false;
    boolean filterSwitchBool = false;
    private AppCompatDelegate mDelegate;

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.activity_settings);

        filter = findPreference("filterPref");
        saveOffline = findPreference("saveOffline");
        extendetView = findPreference("extendetView");
        filterSwitch = findPreference("switch_preference");

        //settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings = getSharedPreferences(PREFS_NAME, 0);

        filter.setOnPreferenceChangeListener(this);
        saveOffline.setOnPreferenceChangeListener(this);
        extendetView.setOnPreferenceChangeListener(this);
        filterSwitch.setOnPreferenceChangeListener(this);

        filter.setDefaultValue(settings.getString(FILTER_KEY, filterString));
        //filter.setDefaultValue(settings.getInt(FILTER_KEY, filterInt));
        saveOffline.setDefaultValue(settings.getBoolean(SAVE_KEY, saveOfflineBool));
        extendetView.setDefaultValue(settings.getBoolean(EXTENDET_KEY, extendetViewBool));
        filterSwitch.setDefaultValue(settings.getBoolean(FILTER_SWITCH_KEY, filterSwitchBool));

        filter.setSummary(settings.getString(FILTER_KEY, filterString));
        //filter.setSummary(settings.getInt(FILTER_KEY, filterInt));
        filter.setEnabled(settings.getBoolean(FILTER_SWITCH_KEY, filterSwitchBool));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public boolean onPreferenceChange(Preference preference, Object value) {
        SharedPreferences.Editor settingsEdit = settings.edit();

        if (preference.getKey() == filter.getKey()) {

            settingsEdit.putString(FILTER_KEY, (String) value);
            Client.filter = (String) value;
            filter.setSummary((String) value);

            /**
             settingsEdit.putInt(FILTER_KEY, (int) value);
             String[] ar = getResources().getStringArray(R.array.classes);
             int[] ara = getResources().getIntArray(R.array.classes_key);
             MainActivity.filter = ara[(int) value];
             filter.setSummary(ar[(int) value]);
             **/
        } else if (preference.getKey() == saveOffline.getKey()) {
            settingsEdit.putBoolean(SAVE_KEY, (Boolean) value);
            Client.saveOfflineBool = (boolean) value;
        } else if (preference.getKey() == extendetView.getKey()) {
            settingsEdit.putBoolean(EXTENDET_KEY, (Boolean) value);
            Client.extendet = (boolean) value;
        } else if (preference.getKey() == filterSwitch.getKey()) {
            settingsEdit.putBoolean(FILTER_SWITCH_KEY, (Boolean) value);
            Client.useFilter = (boolean) value;
            filter.setEnabled((boolean) value);
        }

        settingsEdit.apply();

        return true;
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
