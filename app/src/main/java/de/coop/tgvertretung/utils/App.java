package de.coop.tgvertretung.utils;

import android.app.Application;

import de.coop.tgvertretung.storage.DataManager;

public class App extends Application {

    private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = new DataManager(this);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}