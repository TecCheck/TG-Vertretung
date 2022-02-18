package de.coop.tgvertretung.utils;

import android.app.Application;

import de.coop.tgvertretung.storage.DataManager;

public class TgvApp extends Application {

    private DataManager dataManager;
    private SettingsWrapper appSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = new DataManager(this);
        appSettings = new SettingsWrapper(this);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SettingsWrapper getAppSettings() {
        return appSettings;
    }
}