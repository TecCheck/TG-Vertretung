package de.coop.tgvertretung;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    Preference filter = null;
    Preference extendedView = null;
    Preference filterSwitch = null;
    Preference showText = null;
    Preference showClientRefresh = null;
    Preference showServerRefresh = null;
    Preference showAB = null;
    Preference useOldLayout = null;
    Preference rainbow = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = AppCompatDelegate.create(this, null).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.activity_settings);
        filter = findPreference("filterPref");
        extendedView = findPreference("extendedView");
        filterSwitch = findPreference("switch_preference");
        showText = findPreference("showText");
        showClientRefresh = findPreference("showClientRefresh");
        showServerRefresh = findPreference("showServerRefresh");
        showAB = findPreference("showAB");
        useOldLayout = findPreference("oldLayout");
        rainbow = findPreference("rainbow");

        //settings = PreferenceManager.getDefaultSharedPreferences(this);

        filter.setOnPreferenceChangeListener(this);
        extendedView.setOnPreferenceChangeListener(this);
        filterSwitch.setOnPreferenceChangeListener(this);
        showText.setOnPreferenceChangeListener(this);
        showClientRefresh.setOnPreferenceChangeListener(this);
        showServerRefresh.setOnPreferenceChangeListener(this);
        showAB.setOnPreferenceChangeListener(this);
        useOldLayout.setOnPreferenceChangeListener(this);
        rainbow.setOnPreferenceChangeListener(this);

        filter.setDefaultValue(Settings.settings.filter);
        //filter.setDefaultValue(settings.getInt(FILTER_KEY, filterInt));
        extendedView.setDefaultValue(Settings.settings.extended);
        filterSwitch.setDefaultValue(Settings.settings.useFilter);
        showText.setDefaultValue(Settings.settings.showText);
        showClientRefresh.setDefaultValue(Settings.settings.showClientRefresh);
        showServerRefresh.setDefaultValue(Settings.settings.showServerRefresh);
        showAB.setDefaultValue(Settings.settings.showAB);
        useOldLayout.setDefaultValue(Settings.settings.useOldLayout);
        rainbow.setDefaultValue(Settings.settings.rainbow);

        filter.setSummary(Settings.settings.filter);
        //filter.setSummary(settings.getInt(FILTER_KEY, filterInt));
        filter.setEnabled(Settings.settings.useFilter);
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

        if (preference.getKey().equals(filter.getKey())) {
            Settings.settings.filter = (String) value;
            filter.setSummary((String) value);
        } else if (preference.getKey().equals(extendedView.getKey())) {
            Settings.settings.extended = (boolean) value;
        } else if (preference.getKey().equals(filterSwitch.getKey())) {
            Settings.settings.useFilter = (boolean) value;
            filter.setEnabled((boolean) value);
        }else if(preference.getKey().equals(showText.getKey())){
            Settings.settings.showText = (boolean) value;
        }else if(preference.getKey().equals(showClientRefresh.getKey())){
            Settings.settings.showClientRefresh = (boolean) value;
        }else if(preference.getKey().equals(showServerRefresh.getKey())){
            Settings.settings.showServerRefresh = (boolean) value;
        }else if(preference.getKey().equals(showAB.getKey())){
            Settings.settings.showAB = (boolean) value;
        }else if(preference.getKey().equals(useOldLayout.getKey())){
            Settings.settings.useOldLayout = (boolean) value;
        }else if(preference.getKey().equals(rainbow.getKey())){
            Settings.settings.rainbow = (boolean) value;
        }

        Settings.save();
        return true;
    }
}
