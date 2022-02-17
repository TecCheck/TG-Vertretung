package de.coop.tgvertretung.storage;

import androidx.lifecycle.LiveData;

import de.coop.tgvertretung.utils.NewTimeTable;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public interface StorageProvider {

    LiveData<TimeTable> readTimeTable();

    void saveTimeTable(TimeTable timeTable);

    LiveData<NewTimeTable> readNewTimeTable();

    void saveNewTimeTable(NewTimeTable newTimeTable);

    LiveData<SubjectSymbols> readSubjectSymbols();

    void saveSubjectSymbols(SubjectSymbols subjectSymbols);
}
