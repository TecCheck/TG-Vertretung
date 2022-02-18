package de.coop.tgvertretung.storage;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.coop.tgvertretung.utils.Schedule;
import de.coop.tgvertretung.utils.ScheduleSerializer;
import de.coop.tgvertretung.utils.SubjectSymbols;
import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;
import de.sematre.tg.TimeTable;
import de.sematre.tg.Week;

public class JsonStorageProvider implements StorageProvider {

    private static final String FILE_TIMETABLE = "timetable.json";
    private static final String FILE_SCHEDULE = "schedule.json";
    private static final String FILE_SUBJECT_SYMBOLS = "subject_symbols.json";

    private static final String KEY_DATE = "date";
    private static final String KEY_WEEK = "week";
    private static final String KEY_ENTRIES = "entries";
    private static final String KEY_CLASS = "class";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_REPLACEMENT_SUBJECT = "replacement_subject";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_REPLACEMENT_ROOM = "replacement_room";
    private static final String KEY_ROOM = "room";
    private static final String KEY_TEXT = "text";

    private final Context context;
    private final ScheduledExecutorService executor;

    public JsonStorageProvider(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public TimeTable readTimeTableSync() {
        try {
            JsonObject jsonTimeTable = readJsonFile(FILE_TIMETABLE).getAsJsonObject();
            Date date = new Date(jsonTimeTable.get(KEY_DATE).getAsLong());
            JsonArray jsonEntries = jsonTimeTable.getAsJsonArray(KEY_ENTRIES);

            ArrayList<Table> tables = new ArrayList<>(jsonEntries.size());
            for (JsonElement element : jsonEntries)
                tables.add(getTable(element.getAsJsonObject()));

            if (!tables.isEmpty())
                return new TimeTable(date, tables);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public LiveData<TimeTable> readTimeTable() {
        MutableLiveData<TimeTable> liveData = new MutableLiveData<>();
        executor.schedule(() -> {
            try {
                JsonObject jsonTimeTable = readJsonFile(FILE_TIMETABLE).getAsJsonObject();
                Date date = new Date(jsonTimeTable.get(KEY_DATE).getAsLong());
                JsonArray jsonEntries = jsonTimeTable.getAsJsonArray(KEY_ENTRIES);

                ArrayList<Table> tables = new ArrayList<>(jsonEntries.size());
                for (JsonElement element : jsonEntries)
                    tables.add(getTable(element.getAsJsonObject()));

                if (!tables.isEmpty())
                    liveData.postValue(new TimeTable(date, tables));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);

        return liveData;
    }

    public void saveTimeTable(TimeTable timeTable) {
        executor.schedule(() -> {
            JsonObject jsonTimeTable = new JsonObject();
            jsonTimeTable.addProperty(KEY_DATE, timeTable.getDate().getTime());

            JsonArray jsonEntries = new JsonArray();
            for (Table table : timeTable.getTables())
                jsonEntries.add(getJsonTable(table));

            jsonTimeTable.add(KEY_ENTRIES, jsonEntries);

            try {
                writeJsonFile(jsonTimeTable, FILE_TIMETABLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
    }

    public LiveData<Schedule> readSchedule() {
        MutableLiveData<Schedule> liveData = new MutableLiveData<>();
        executor.schedule(() -> {
            try {
                JsonArray jsonNewTimeTable = readJsonFile(FILE_SCHEDULE).getAsJsonArray();
                Schedule schedule = ScheduleSerializer.getSchedule(jsonNewTimeTable);
                liveData.postValue(schedule);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
        return liveData;
    }

    public void saveSchedule(Schedule schedule) {
        executor.schedule(() -> {
            try {
                JsonArray jsonNewTimeTable = schedule.getJson();
                writeJsonFile(jsonNewTimeTable, FILE_SCHEDULE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
    }

    public LiveData<SubjectSymbols> readSubjectSymbols() {
        MutableLiveData<SubjectSymbols> liveData = new MutableLiveData<>();
        executor.schedule(() -> {
            try {
                SubjectSymbols subjectSymbols = new SubjectSymbols();
                subjectSymbols.readJson(readJson(FILE_SUBJECT_SYMBOLS));
                liveData.postValue(subjectSymbols);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
        return liveData;
    }

    public void saveSubjectSymbols(SubjectSymbols subjectSymbols) {
        executor.schedule(() -> {
            try {
                writeJsonFile(subjectSymbols.getJson(), FILE_SUBJECT_SYMBOLS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
    }

    private JsonObject getJsonTable(Table table) {
        JsonObject jsonTable = new JsonObject();
        jsonTable.addProperty(KEY_DATE, table.getDate().getTime());
        jsonTable.addProperty(KEY_WEEK, table.getWeek().getLetter());

        JsonArray jsonEntries = new JsonArray();
        for (TableEntry tableEntry : table.getTableEntries()) {
            JsonObject jsonEntry = new JsonObject();
            jsonEntry.addProperty(KEY_CLASS, tableEntry.getSchoolClass());
            jsonEntry.addProperty(KEY_TIME, tableEntry.getTime());
            jsonEntry.addProperty(KEY_TYPE, tableEntry.getType());
            jsonEntry.addProperty(KEY_REPLACEMENT_SUBJECT, tableEntry.getReplacementSubject());
            jsonEntry.addProperty(KEY_SUBJECT, tableEntry.getSubject());
            jsonEntry.addProperty(KEY_REPLACEMENT_ROOM, tableEntry.getReplacementRoom());
            jsonEntry.addProperty(KEY_ROOM, tableEntry.getRoom());
            jsonEntry.addProperty(KEY_TEXT, tableEntry.getText());
            jsonEntries.add(jsonEntry);
        }

        jsonTable.add(KEY_ENTRIES, jsonEntries);
        return jsonTable;
    }

    private Table getTable(JsonObject jsonTable) {
        Table table = new Table();
        table.setDate(new Date(jsonTable.get(KEY_DATE).getAsLong()));
        table.setWeek(Week.getWeek(jsonTable.get(KEY_WEEK).getAsString()));

        JsonArray jsonEntries = jsonTable.getAsJsonArray(KEY_ENTRIES);
        ArrayList<TableEntry> entries = new ArrayList<>(jsonEntries.size());
        for (JsonElement jsonEntry : jsonEntries) {
            JsonObject jsonObject = jsonEntry.getAsJsonObject();
            TableEntry entry = new TableEntry();
            entry.setSchoolClass(jsonObject.get(KEY_CLASS).getAsString());
            entry.setTime(jsonObject.get(KEY_TIME).getAsString());
            entry.setType(jsonObject.get(KEY_TYPE).getAsString());
            entry.setReplacementSubject(jsonObject.get(KEY_REPLACEMENT_SUBJECT).getAsString());
            entry.setSubject(jsonObject.get(KEY_SUBJECT).getAsString());
            entry.setReplacementRoom(jsonObject.get(KEY_REPLACEMENT_ROOM).getAsString());
            entry.setRoom(jsonObject.get(KEY_ROOM).getAsString());
            entry.setText(jsonObject.get(KEY_TEXT).getAsString());
            entries.add(entry);
        }

        table.setTableEntries(entries);
        return table;
    }

    private void writeJsonFile(JsonElement jsonElement, String filename) throws IOException {
        writeJsonFile(jsonElement.toString(), filename);
    }

    private void writeJsonFile(String json, String filename) throws IOException {
        String path = context.getFilesDir().getAbsolutePath();
        File dir = new File(path);
        dir.mkdirs();

        File file = new File(path + File.separator + filename);
        file.createNewFile();
        writeFile(file, json);
    }

    private void writeFile(File file, String text) throws IOException {
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(text);
        myWriter.close();
    }

    private JsonElement readJsonFile(String filename) throws IOException {
        JsonParser parser = new JsonParser();
        return parser.parse(readJson(filename));
    }

    private String readJson(String filename) throws IOException {
        String path = context.getFilesDir().getAbsolutePath() + File.separator + filename;
        File file = new File(path);
        if (!file.exists())
            return null;

        return readFile(file);
    }

    private String readFile(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine())
            stringBuilder.append(scanner.nextLine());

        scanner.close();
        return stringBuilder.toString();
    }
}
