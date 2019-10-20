package de.coop.tgvertretung.utils;

import com.google.gson.JsonArray;

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

    public NewTimeTable(HashMap<Week, ArrayList<ArrayList<TimeTableDayEntry>>> weeks){
        this.weeks = weeks;
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
        return NewTimeTableSerializer.getJsonTimeTable(weeks.get(Week.A), weeks.get(Week.B));
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