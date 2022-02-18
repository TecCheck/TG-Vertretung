package de.coop.tgvertretung.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.sematre.tg.Week;

public class ScheduleSerializer {

    public static JsonObject getJsonEntry(Schedule.ScheduleDayEntry entry, Schedule.ScheduleDayEntry entryB) {
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

        return jsonObject;
    }

    public static JsonArray getJsonDay(ArrayList<Schedule.ScheduleDayEntry> day, ArrayList<Schedule.ScheduleDayEntry> dayB) {
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
                    i++;
                } else {
                    jsonObject1.addProperty("d", false);
                    jsonArray.add(jsonObject1);
                }
            } else {
                jsonObject1.addProperty("d", false);
                jsonArray.add(jsonObject1);
            }
        }

        return jsonArray;
    }

    public static JsonArray getJsonSchedule(ArrayList<ArrayList<Schedule.ScheduleDayEntry>> timeTable, ArrayList<ArrayList<Schedule.ScheduleDayEntry>> timeTableB) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < timeTable.size(); i++) {
            jsonArray.add(getJsonDay(timeTable.get(i), timeTableB.get(i)));
        }

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

    public static Schedule.ScheduleDayEntry[] getScheduleDayEntries(JsonObject jsonObject){
        Schedule.ScheduleDayEntry entryA = new Schedule.ScheduleDayEntry();
        Schedule.ScheduleDayEntry entryB = new Schedule.ScheduleDayEntry();

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

        return new Schedule.ScheduleDayEntry[]{entryA, entryB};
    }

    public static ArrayList<Schedule.ScheduleDayEntry>[] getScheduleDay(JsonArray jsonArray){
        ArrayList<Schedule.ScheduleDayEntry> tableA = new ArrayList<>();
        ArrayList<Schedule.ScheduleDayEntry> tableB = new ArrayList<>();

        for(JsonElement element : jsonArray){
            JsonObject jsonObject = element.getAsJsonObject();

            tableA.add(getScheduleDayEntries(jsonObject)[0]);
            tableB.add(getScheduleDayEntries(jsonObject)[1]);

            if((!jsonObject.has("d")) || jsonObject.get("d").getAsBoolean()){
                //double
                tableA.add(getScheduleDayEntries(jsonObject)[0]);
                tableB.add(getScheduleDayEntries(jsonObject)[1]);
            }
        }

        return new ArrayList[]{tableA, tableB};
    }

    public static Schedule getSchedule(JsonArray jsonArray){
        ArrayList<ArrayList<Schedule.ScheduleDayEntry>> weekA = new ArrayList<>();
        ArrayList<ArrayList<Schedule.ScheduleDayEntry>> weekB = new ArrayList<>();

        for(JsonElement element : jsonArray) {
            JsonArray jsonArray1 = element.getAsJsonArray();

            weekA.add(getScheduleDay(jsonArray1)[0]);
            weekB.add(getScheduleDay(jsonArray1)[1]);
        }

        HashMap<Week, ArrayList<ArrayList<Schedule.ScheduleDayEntry>>> weeks = new HashMap<>();
        weeks.put(Week.A, weekA);
        weeks.put(Week.B, weekB);

        return new Schedule(weeks);
    }
}