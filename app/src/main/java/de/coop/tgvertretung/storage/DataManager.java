package de.coop.tgvertretung.storage;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;

import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public class DataManager implements Downloader.LoadFinishedListener {

    private final StorageProvider storage;
    private final Downloader downloader;

    private final MutableLiveData<TimeTable> timeTable;
    private final MutableLiveData<NewTimeTable> newTimeTable;
    private final MutableLiveData<SubjectSymbols> subjectSymbols;

    public DataManager(Context context) {
        this.storage = new JsonStorageProvider(context);
        this.downloader = new Downloader(this);

        this.timeTable = new MutableLiveData<>();
        this.newTimeTable = new MutableLiveData<>();
        this.subjectSymbols = new MutableLiveData<>();
    }

    public LiveData<TimeTable> getTimeTable(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.timeTable.getValue() == null)
            storage.readTimeTable().observe(lifecycleOwner, timeTable::postValue);
        else
            Log.d("DataManager", "TimeTable cached");
        return timeTable;
    }

    public LiveData<NewTimeTable> getNewTimeTable(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.newTimeTable.getValue() == null)
            storage.readNewTimeTable().observe(lifecycleOwner, newTimeTable::postValue);
        else
            Log.d("DataManager", "NewTimeTable cached");
        return newTimeTable;
    }

    public LiveData<SubjectSymbols> getSubjectSymbols(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.subjectSymbols.getValue() == null)
            storage.readSubjectSymbols().observe(lifecycleOwner, subjectSymbols::postValue);
        else
            Log.d("DataManager", "SubjectSymbols cached");
        return subjectSymbols;
    }

    public boolean downloadTimeTable(String username, String password, Downloader.ResultListener listener) {
        TimeTable timeTable = this.timeTable.getValue();
        Date currentNewest = timeTable == null ? new Date() : timeTable.getDate();
        return downloader.download(currentNewest, username, password, listener);
    }

    public void setNewTimeTable(NewTimeTable newTimeTable) {
        storage.saveNewTimeTable(newTimeTable);
        this.newTimeTable.postValue(newTimeTable);
    }

    public void setSubjectSymbols(SubjectSymbols symbols) {
        storage.saveSubjectSymbols(symbols);
        this.subjectSymbols.postValue(symbols);
    }

    @Override
    public void loadFinished(Downloader.DownloadResult result, TimeTable timeTable) {
        if (result != Downloader.DownloadResult.SUCCESS)
            return;

        this.timeTable.postValue(timeTable);
        storage.saveTimeTable(timeTable);
    }
}
