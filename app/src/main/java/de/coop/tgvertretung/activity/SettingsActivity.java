package de.coop.tgvertretung.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import de.coop.tgvertretung.R;
import de.coop.tgvertretung.utils.Settings;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private Preference filterSwitch = null;
    private Preference filter = null;

    private Preference extendedView = null;
    private Preference showText = null;
    private Preference showAB = null;
    private Preference showClientRefresh = null;
    private Preference showServerRefresh = null;

    private ListPreference themeMode = null;
    private Preference twoLineLabel = null;
    private Preference rainbow = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = AppCompatDelegate.create(this, null).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.activity_settings);
        filterSwitch = findPreference("filter_switch");
        filter = findPreference("filter");

        extendedView = findPreference("extended_view");
        showText = findPreference("show_text");
        showAB = findPreference("show_ab");
        showClientRefresh = findPreference("show_client_refresh");
        showServerRefresh = findPreference("show_server_refresh");

        themeMode = (ListPreference) findPreference("theme_mode");
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

        themeMode.setOnPreferenceChangeListener(this);
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

        themeMode.setDefaultValue(Settings.settings.themeMode);
        twoLineLabel.setDefaultValue(Settings.settings.twoLineLabel);
        rainbow.setDefaultValue(Settings.settings.rainbow);

        filter.setSummary(Settings.settings.filter);
        themeMode.setSummary(getResources().getStringArray(R.array.setting_theme_modes)[Settings.settings.themeMode]);
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
        } else if (preference.getKey().equals(rainbow.getKey())) {
            Settings.settings.rainbow = (boolean) value;
        } else if (preference.getKey().equals(twoLineLabel.getKey())) {
            Settings.settings.twoLineLabel = (boolean) value;
        } else if (preference.getKey().equals(themeMode.getKey())) {
            Settings.settings.themeMode = themeMode.findIndexOfValue((String) value);
            themeMode.setSummary(getResources().getStringArray(R.array.setting_theme_modes)[Settings.settings.themeMode]);
            AppCompatDelegate.setDefaultNightMode(Settings.settings.themeMode);
        }

        Settings.save();
        return true;
    }
}