package de.coop.tgvertretung;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import java.util.Set;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    Preference filter = null;
    Preference extendetView = null;
    Preference filterSwitch = null;
    Preference showText = null;
    Preference showClientRefresh = null;
    Preference showServerRefersh = null;

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
        extendetView = findPreference("extendetView");
        filterSwitch = findPreference("switch_preference");
        showText = findPreference("showText");
        showClientRefresh = findPreference("showClientRefresh");
        showServerRefersh = findPreference("showServerRefresh");

        //settings = PreferenceManager.getDefaultSharedPreferences(this);

        filter.setOnPreferenceChangeListener(this);
        extendetView.setOnPreferenceChangeListener(this);
        filterSwitch.setOnPreferenceChangeListener(this);
        showText.setOnPreferenceChangeListener(this);
        showClientRefresh.setOnPreferenceChangeListener(this);
        showServerRefersh.setOnPreferenceChangeListener(this);

        filter.setDefaultValue(Settings.settings.filter);
        //filter.setDefaultValue(settings.getInt(FILTER_KEY, filterInt));
        extendetView.setDefaultValue(Settings.settings.extendet);
        filterSwitch.setDefaultValue(Settings.settings.useFilter);
        showText.setDefaultValue(Settings.settings.showText);
        showClientRefresh.setDefaultValue(Settings.settings.showClientRefersh);
        showServerRefersh.setDefaultValue(Settings.settings.showServerRefresh);

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
        if (preference.getKey() == filter.getKey()) {
            Settings.settings.filter = (String) value;
            filter.setSummary((String) value);
        } else if (preference.getKey() == extendetView.getKey()) {
            Settings.settings.extendet = (boolean) value;
        } else if (preference.getKey() == filterSwitch.getKey()) {
            Settings.settings.useFilter = (boolean) value;
            filter.setEnabled((boolean) value);
        }else if(preference.getKey() == showText.getKey()){
            Settings.settings.showText = (boolean) value;
        }else if(preference.getKey() == showClientRefresh.getKey()){
            Settings.settings.showClientRefersh = (boolean) value;
        }else if(preference.getKey() == showServerRefersh.getKey()){
            Settings.settings.showServerRefresh = (boolean) value;
        }

        Settings.save();
        return true;
    }
}
