package de.coop.tgvertretung.storage;

import androidx.lifecycle.LiveData;

import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.TimeTable;

public interface StorageProvider {

    LiveData<TimeTable> readTimeTable();

    void saveTimeTable(TimeTable timeTable);

    LiveData<Schedule> readSchedule();

    void saveSchedule(Schedule schedule);

    LiveData<SubjectSymbols> readSubjectSymbols();

    void saveSubjectSymbols(SubjectSymbols subjectSymbols);
}
