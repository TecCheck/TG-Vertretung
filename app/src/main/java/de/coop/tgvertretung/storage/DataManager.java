package de.coop.tgvertretung.storage;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;

import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public class DataManager implements Downloader.LoadFinishedListener {

    private final Context context;
    private final StorageProvider storage;
    private final Downloader downloader;

    private final MutableLiveData<TimeTable> timeTable;
    private final MutableLiveData<NewTimeTable> newTimeTable;
    private final MutableLiveData<SubjectSymbols> subjectSymbols;

    public DataManager(Context context) {
        this.context = context;
        this.storage = new JsonStorageProvider(context);
        this.downloader = new Downloader(this);

        this.timeTable = new MutableLiveData<>();
        this.newTimeTable = new MutableLiveData<>();
        this.subjectSymbols = new MutableLiveData<>();
    }

    public LiveData<TimeTable> getTimeTable(LifecycleOwner lifecycleOwner) {
        storage.readTimeTable().observe(lifecycleOwner, timeTable::postValue);
        return timeTable;
    }

    public LiveData<NewTimeTable> getNewTimeTable() {
        return storage.readNewTimeTable();
    }

    public LiveData<SubjectSymbols> getSubjectSymbols() {
        return storage.readSubjectSymbols();
    }

    public boolean downloadTimeTable(String username, String password, Downloader.ResultListener listener) {
        TimeTable timeTable = this.timeTable.getValue();
        Date currentNewest = timeTable == null ? new Date() : timeTable.getDate();
        return downloader.download(currentNewest, username, password, listener);
    }

    @Override
    public void loadFinished(Downloader.DownloadResult result, TimeTable timeTable) {
        if (result != Downloader.DownloadResult.SUCCESS)
            return;

        this.timeTable.postValue(timeTable);
        storage.saveTimeTable(timeTable);
    }
}
