package de.coop.tgvertretung.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class TimeTable implements Serializable {

    private static final long serialVersionUID = -6062123053432180842L;

    public ArrayList<TimeTableDay> days = new ArrayList<>();

    public TimeTable() {
        for (int i = 0; i < 5; i++) {
            days.add(new TimeTableDay());
        }
    }

    public void setDay(int day, TimeTableDay entry) {
        days.set(day, entry);
    }

    public TimeTableDay getDay(int index) {
        try {
            return days.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSize() {
        return days.size();
    }

    public static class TimeTableEntry implements Serializable {

        private static final long serialVersionUID = -7162123053432181952L;

        String subject = "";
        String room = "";
        String teacher = "";

        public TimeTableEntry(String subject, String room, String teacher) {
            this.subject = subject;
            this.room = room;
            this.teacher = teacher;
        }

        public String getSubject() {
            return subject;
        }

        public String getRoom() {
            return room;
        }

        public String getTeacher() {
            return teacher;
        }
    }

    public class TimeTableDay implements Serializable {

        private static final long serialVersionUID = -7162123053432180842L;

        public ArrayList<TimeTableEntry> entries = new ArrayList<>();

        public TimeTableDay() {
            for (int i = 0; i < 11; i++) {
                entries.add(null);
            }
        }

        public void setEntry(int hour, TimeTableEntry entry) {
            if (entries.size() <= hour) {
                int i = entries.size() - hour;
                while (i > 0) {
                    entries.add(null);
                    i--;
                }
                entries.add(entry);
            } else {
                entries.set(hour, entry);
            }
        }

        public TimeTableEntry getEntry(int hour) {
            try {
                return entries.get(hour);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public int getSize() {
            return entries.size();
        }
    }

}
