package de.coop.tgvertretung.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.sematre.tg.Week;

public class NewTimeTableSerializer {

    public static JsonObject getJsonEntry(NewTimeTable.TimeTableDayEntry entry, NewTimeTable.TimeTableDayEntry entryB) {
        JsonObject jsonObject = new JsonObject();
        if (notEmpty(entry.subject)) {
            jsonObject.addProperty("s", entry.subject);

            if (notEmpty(entry.room)) jsonObject.addProperty("r", entry.room);
            if (notEmpty(entry.teacher)) jsonObject.addProperty("t", entry.teacher);
        }

        if (entryB != null) {
            if (!entry.subject.equals(entryB.subject))
                jsonObject.addProperty("sB", entryB.subject);
            if (!entry.room.equals(entryB.room))
                jsonObject.addProperty("rB", entryB.room);
            if (!entry.teacher.equals(entryB.teacher))
                jsonObject.addProperty("tB", entryB.teacher);
        }

        Log.d("getJsonEntry", jsonObject.toString());
        return jsonObject;
    }

    public static JsonArray getJsonDay(ArrayList<NewTimeTable.TimeTableDayEntry> day, ArrayList<NewTimeTable.TimeTableDayEntry> dayB) {
        JsonArray jsonArray = new JsonArray();
        ArrayList<JsonObject> objects = new ArrayList<>();
        for (int i = 0; i < day.size(); i++) {
            objects.add(getJsonEntry(day.get(i), dayB.get(i)));
        }

        for (int i = 0; i < objects.size(); i++) {
            JsonObject jsonObject1 = objects.get(i);

            if (!(i + 1 >= objects.size())) {
                JsonObject jsonObject2 = objects.get(i + 1);
                if (equalJson(jsonObject1, jsonObject2)) {
                    jsonArray.add(jsonObject1);
                    Log.d("Equals", jsonObject1 + ", " + jsonObject2);
                    i++;
                } else {
                    Log.d("Not Equals", jsonObject1 + ", " + jsonObject2);

                    jsonObject1.addProperty("d", false);
                    jsonArray.add(jsonObject1);
                }
            } else {
                jsonObject1.addProperty("d", false);
                jsonArray.add(jsonObject1);
            }
        }

        Log.d("getJsonDay", jsonArray.toString());
        return jsonArray;
    }

    public static JsonArray getJsonTimeTable(ArrayList<ArrayList<NewTimeTable.TimeTableDayEntry>> timeTable, ArrayList<ArrayList<NewTimeTable.TimeTableDayEntry>> timeTableB) {
        Log.d("getJsonTimeTable", "Size: " + timeTable.size());

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < timeTable.size(); i++) {
            Log.d("getJsonTimeTable", "Index: " + i);
            jsonArray.add(getJsonDay(timeTable.get(i), timeTableB.get(i)));
        }

        Log.d("getJsonTimeTable", jsonArray.toString());
        return jsonArray;
    }

    public static boolean notEmpty(String string) {
        return !(string == null || string.isEmpty());
    }

    public static boolean equalJson(JsonObject jsonObject1, JsonObject jsonObject2) {
        if (!(jsonObject1 == null || jsonObject2 == null)) {
            return (jsonObject1.toString().equals(jsonObject2.toString()));
        } else if (jsonObject1 == null && jsonObject2 == null) {
            return true;
        }

        return false;
    }

    public static NewTimeTable.TimeTableDayEntry[] getTimeTableDayEntries (JsonObject jsonObject){
        NewTimeTable.TimeTableDayEntry entryA = new NewTimeTable.TimeTableDayEntry();
        NewTimeTable.TimeTableDayEntry entryB = new NewTimeTable.TimeTableDayEntry();

        if(jsonObject.has("s")) entryA.subject = jsonObject.get("s").getAsString();
        else entryA.subject = "";

        if(jsonObject.has("r")) entryA.room = jsonObject.get("r").getAsString();
        else entryA.room = "";

        if(jsonObject.has("t")) entryA.teacher = jsonObject.get("t").getAsString();
        else entryA.teacher = "";

        if(jsonObject.has("sB")) entryB.subject = jsonObject.get("sB").getAsString();
        else entryB.subject = entryA.subject;

        if(jsonObject.has("rB")) entryB.room = jsonObject.get("rB").getAsString();
        else entryB.room = entryA.room;

        if(jsonObject.has("tB")) entryB.teacher = jsonObject.get("tB").getAsString();
        else entryB.teacher = entryA.teacher;

        return new NewTimeTable.TimeTableDayEntry[]{entryA, entryB};
    }

    public static ArrayList<NewTimeTable.TimeTableDayEntry>[] getTimeTableDay(JsonArray jsonArray){
        ArrayList<NewTimeTable.TimeTableDayEntry> tableA = new ArrayList<>();
        ArrayList<NewTimeTable.TimeTableDayEntry> tableB = new ArrayList<>();

        for(JsonElement element : jsonArray){
            JsonObject jsonObject = element.getAsJsonObject();

            tableA.add(getTimeTableDayEntries(jsonObject)[0]);
            tableB.add(getTimeTableDayEntries(jsonObject)[1]);

            if((!jsonObject.has("d")) || jsonObject.get("d").getAsBoolean()){
                //double
                tableA.add(getTimeTableDayEntries(jsonObject)[0]);
                tableB.add(getTimeTableDayEntries(jsonObject)[1]);
            }
        }

        return new ArrayList[]{tableA, tableB};
    }

    public static NewTimeTable getTimeTable(JsonArray jsonArray){
        ArrayList<ArrayList<NewTimeTable.TimeTableDayEntry>> weekA = new ArrayList<>();
        ArrayList<ArrayList<NewTimeTable.TimeTableDayEntry>> weekB = new ArrayList<>();

        for(JsonElement element : jsonArray) {
            JsonArray jsonArray1 = element.getAsJsonArray();

            weekA.add(getTimeTableDay(jsonArray1)[0]);
            weekB.add(getTimeTableDay(jsonArray1)[1]);
        }

        HashMap<Week, ArrayList<ArrayList<NewTimeTable.TimeTableDayEntry>>> weeks = new HashMap<>();
        weeks.put(Week.A, weekA);
        weeks.put(Week.B, weekB);

        return new NewTimeTable(weeks);
    }
}