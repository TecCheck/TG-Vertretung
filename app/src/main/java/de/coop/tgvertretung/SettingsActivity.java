package de.coop.tgvertretung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = AppCompatDelegate.create(this, null).getSupportActionBar();
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
        settings = getSharedPreferences(getString(R.string.settings_name), 0);

        filter.setOnPreferenceChangeListener(this);
        saveOffline.setOnPreferenceChangeListener(this);
        extendetView.setOnPreferenceChangeListener(this);
        filterSwitch.setOnPreferenceChangeListener(this);

        filter.setDefaultValue(settings.getString(getString(R.string.settings_filter), filterString));
        //filter.setDefaultValue(settings.getInt(FILTER_KEY, filterInt));
        saveOffline.setDefaultValue(settings.getBoolean(getString(R.string.settings_saveofflinebool), saveOfflineBool));
        extendetView.setDefaultValue(settings.getBoolean(getString(R.string.settings_extendet), extendetViewBool));
        filterSwitch.setDefaultValue(settings.getBoolean(getString(R.string.settings_filterswitch), filterSwitchBool));

        filter.setSummary(settings.getString(getString(R.string.settings_filter), filterString));
        //filter.setSummary(settings.getInt(FILTER_KEY, filterInt));
        filter.setEnabled(settings.getBoolean(getString(R.string.settings_filterswitch), filterSwitchBool));
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

            settingsEdit.putString(getString(R.string.settings_filter), (String) value);
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
            settingsEdit.putBoolean(getString(R.string.settings_saveofflinebool), (Boolean) value);
            Client.saveOfflineBool = (boolean) value;
        } else if (preference.getKey() == extendetView.getKey()) {
            settingsEdit.putBoolean(getString(R.string.settings_extendet), (Boolean) value);
            Client.extendet = (boolean) value;
        } else if (preference.getKey() == filterSwitch.getKey()) {
            settingsEdit.putBoolean(getString(R.string.settings_filterswitch), (Boolean) value);
            Client.useFilter = (boolean) value;
            filter.setEnabled((boolean) value);
        }

        settingsEdit.apply();
        return true;
    }
}
