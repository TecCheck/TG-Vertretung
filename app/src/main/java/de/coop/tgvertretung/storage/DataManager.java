package de.coop.tgvertretung.storage;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;

import de.coop.tgvertretung.utils.Downloader;
import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public class DataManager implements Downloader.LoadFinishedListener {

    private final StorageProvider storage;
    private final Downloader downloader;

    private final MutableLiveData<TimeTable> timeTable;
    private final MutableLiveData<Schedule> schedule;
    private final MutableLiveData<SubjectSymbols> subjectSymbols;

    public DataManager(Context context) {
        this.storage = new JsonStorageProvider(context);
        this.downloader = new Downloader(this);

        this.timeTable = new MutableLiveData<>();
        this.schedule = new MutableLiveData<>();
        this.subjectSymbols = new MutableLiveData<>();
    }

    public LiveData<TimeTable> getTimeTable(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.timeTable.getValue() == null)
            storage.readTimeTable().observe(lifecycleOwner, timeTable::postValue);
        return timeTable;
    }

    public LiveData<Schedule> getSchedule(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.schedule.getValue() == null)
            storage.readSchedule().observe(lifecycleOwner, schedule::postValue);
        return schedule;
    }

    public LiveData<SubjectSymbols> getSubjectSymbols(LifecycleOwner lifecycleOwner, boolean forceReload) {
        if (forceReload || this.subjectSymbols.getValue() == null)
            storage.readSubjectSymbols().observe(lifecycleOwner, subjectSymbols::postValue);
        return subjectSymbols;
    }

    public boolean downloadTimeTable(String username, String password, Downloader.ResultListener listener) {
        TimeTable timeTable = this.timeTable.getValue();
        Date currentNewest = timeTable == null ? new Date() : timeTable.getDate();
        return downloader.download(currentNewest, username, password, listener);
    }

    public void setSchedule(Schedule schedule) {
        storage.saveSchedule(schedule);
        this.schedule.postValue(schedule);
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
