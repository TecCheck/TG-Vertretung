package de.coop.tgvertretung.storage;

import androidx.lifecycle.LiveData;

import de.sematre.tg.TimeTable;

public interface StorageProvider {

    LiveData<TimeTable> readTimeTable();

    void saveTimeTable(TimeTable timeTable);
}
