package de.coop.tgvertretung.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.sematre.tg.Week;

public class NewTimeTable implements Serializable {

    private static final long serialVersionUID = 7172123053432181952L;
    private  HashMap<Week, ArrayList<ArrayList<TimeTableDayEntry>>> weeks = new HashMap<>();

    public NewTimeTable() {
        init();
    }

    private void init() {
        ArrayList<ArrayList<TimeTableDayEntry>> arrayList = new ArrayList<>();
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        weeks.put(Week.A, arrayList);

        arrayList = new ArrayList<>();
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        arrayList.add(new ArrayList<>());
        weeks.put(Week.B, arrayList);
    }

    public TimeTableDayEntry getEntry(Week week, int day, int hour) {
        if (weeks.containsKey(week) && weeks.get(week).size() > day && weeks.get(week).get(day).size() > hour) {
            return weeks.get(week).get(day).get(hour);
        }

        return null;
    }

    public void setEntry(Week week, int day, int hour, TimeTableDayEntry entry) {
        if (weeks.get(week).get(day).size() <= hour) {
            weeks.get(week).get(day).add(entry);
        } else {
            weeks.get(week).get(day).set(hour, entry);
        }
    }

    public void removeEntry(Week week, int day, int hour) {
        if (weeks.containsKey(week) && weeks.get(week).size() > day && weeks.get(week).get(day).size() > hour) {
            weeks.get(week).get(day).remove(hour);
        }
    }

    public int getDaySize(Week week, int day) {
        if (weeks.containsKey(week) && weeks.get(week).size() > day) {
            return weeks.get(week).get(day).size();
        }

        return -1;
    }

    public JsonArray getJson() {
        return getJsonTimeTable(weeks.get(Week.A), weeks.get(Week.B));
    }

    public static JsonObject getJsonEntry(TimeTableDayEntry entry, TimeTableDayEntry entryB) {
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

    public static JsonArray getJsonDay(ArrayList<TimeTableDayEntry> day, ArrayList<TimeTableDayEntry> dayB) {
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

    public static JsonArray getJsonTimeTable(ArrayList<ArrayList<TimeTableDayEntry>> timeTable, ArrayList<ArrayList<TimeTableDayEntry>> timeTableB) {
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

    public static class TimeTableDayEntry implements Serializable {
        private static final long serialVersionUID = 1235523053432181952L;
        public String subject;
        public String room;
        public String teacher;

        public boolean equals(TimeTableDayEntry entry) {
            if (entry.subject != null) {
                if (!entry.subject.equals(subject))
                    return false;
            } else if (subject != null) {
                return false;
            }

            if (entry.room != null) {
                if (!entry.room.equals(room))
                    return false;
            } else if (room != null) {
                return false;
            }

            if (entry.teacher != null) {
                if (!entry.teacher.equals(teacher))
                    return false;
            } else if (teacher != null) {
                return false;
            }

            return true;
        }
    }
}