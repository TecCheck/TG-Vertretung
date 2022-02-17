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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.sematre.tg.Table;
import de.sematre.tg.TableEntry;
import de.sematre.tg.TimeTable;
import de.sematre.tg.Week;

public class JsonStorageProvider implements StorageProvider {

    private static final String SUBFOLDER = "storage";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String METADATA_FILE = "metadata.json";

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

    public LiveData<TimeTable> readTimeTable() {
        MutableLiveData<TimeTable> liveData = new MutableLiveData<>();
        executor.schedule(() -> {
            File folder = new File(getStorageFolder());
            File[] files = folder.listFiles();

            if (files == null)
                return;

            JsonParser parser = new JsonParser();
            ArrayList<Table> tables = new ArrayList<>(files.length - 1);
            Date date = null;

            for (File file : files) {
                try {
                    JsonObject jsonObject = parser.parse(readFile(file)).getAsJsonObject();
                    if (file.getName().equals(METADATA_FILE)) {
                        date = new Date(jsonObject.get(KEY_DATE).getAsLong());
                    } else {
                        tables.add(getTable(jsonObject));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (date != null && !tables.isEmpty())
                liveData.postValue(new TimeTable(date, tables));
        }, 0, TimeUnit.NANOSECONDS);

        return liveData;
    }

    public void saveTimeTable(TimeTable timeTable) {
        executor.schedule(() -> {
            try {
                JsonObject metadata = new JsonObject();
                metadata.addProperty(KEY_DATE, timeTable.getDate().getTime());
                writeFile(createFile(METADATA_FILE), metadata.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Table table : timeTable.getTables()) {
                try {
                    JsonObject jsonTable = getJsonTable(table);
                    File output = createFile(table.getDate());
                    writeFile(output, jsonTable.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private File createFile(Date date) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
        String name = format.format(date) + ".json";
        return createFile(name);
    }

    private File createFile(String name) throws IOException {
        String folder = getStorageFolder();
        File dir = new File(folder);
        dir.mkdirs();
        File file = new File(folder + File.separator + name);
        file.createNewFile();
        return file;
    }

    private void writeFile(File file, String text) throws IOException {
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(text);
        myWriter.close();
    }

    private String readFile(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine())
            stringBuilder.append(scanner.nextLine());

        scanner.close();
        return stringBuilder.toString();
    }

    private String getStorageFolder() {
        return context.getFilesDir().getAbsolutePath() + File.separator + SUBFOLDER;
    }
}
