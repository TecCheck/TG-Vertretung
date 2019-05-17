package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import java.util.Set;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.Settings;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private Preference filterSwitch = null;
    private Preference filter = null;

    private Preference extendedView = null;
    private Preference showText = null;
    private Preference showAB = null;
    private Preference showClientRefresh = null;
    private Preference showServerRefresh = null;

    private Preference useOldLayout = null;
    private Preference twoLineLabel = null;
    private Preference rainbow = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = AppCompatDelegate.create(this, null).getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.activity_settings);
        filterSwitch = findPreference("switch_preference");
        filter = findPreference("filterPref");

        extendedView = findPreference("extendedView");
        showText = findPreference("showText");
        showAB = findPreference("showAB");
        showClientRefresh = findPreference("showClientRefresh");
        showServerRefresh = findPreference("showServerRefresh");

        useOldLayout = findPreference("oldLayout");
        twoLineLabel = findPreference("two_line_label");
        rainbow = findPreference("rainbow");

        //settings = PreferenceManager.getDefaultSharedPreferences(this);

        filterSwitch.setOnPreferenceChangeListener(this);
        filter.setOnPreferenceChangeListener(this);

        extendedView.setOnPreferenceChangeListener(this);
        showText.setOnPreferenceChangeListener(this);
        showAB.setOnPreferenceChangeListener(this);
        showClientRefresh.setOnPreferenceChangeListener(this);
        showServerRefresh.setOnPreferenceChangeListener(this);

        useOldLayout.setOnPreferenceChangeListener(this);
        twoLineLabel.setOnPreferenceChangeListener(this);
        rainbow.setOnPreferenceChangeListener(this);

        filterSwitch.setDefaultValue(Settings.settings.useFilter);
        filter.setDefaultValue(Settings.settings.filter);
        //filter.setDefaultValue(settings.getInt(FILTER_KEY, filterInt));
        extendedView.setDefaultValue(Settings.settings.extended);
        showText.setDefaultValue(Settings.settings.showText);
        showAB.setDefaultValue(Settings.settings.showAB);
        showClientRefresh.setDefaultValue(Settings.settings.showClientRefresh);
        showServerRefresh.setDefaultValue(Settings.settings.showServerRefresh);

        useOldLayout.setDefaultValue(Settings.settings.useOldLayout);
        twoLineLabel.setDefaultValue(Settings.settings.twoLineLabel);
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
        } else if (preference.getKey().equals(showText.getKey())) {
            Settings.settings.showText = (boolean) value;
        } else if (preference.getKey().equals(showClientRefresh.getKey())) {
            Settings.settings.showClientRefresh = (boolean) value;
        } else if (preference.getKey().equals(showServerRefresh.getKey())) {
            Settings.settings.showServerRefresh = (boolean) value;
        } else if (preference.getKey().equals(showAB.getKey())) {
            Settings.settings.showAB = (boolean) value;
        } else if (preference.getKey().equals(useOldLayout.getKey())) {
            Settings.settings.useOldLayout = (boolean) value;
        } else if (preference.getKey().equals(rainbow.getKey())) {
            Settings.settings.rainbow = (boolean) value;
        } else if (preference.getKey().equals(twoLineLabel.getKey())) {
            Settings.settings.twoLineLabel = (boolean) value;
        }

        Settings.save();
        return true;
    }
}