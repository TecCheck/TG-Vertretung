package de.coop.tgvertretung.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class TimeTable implements Serializable {

    private static final long serialVersionUID = -6062123053432180842L;

    public ArrayList<TimeTableDay> days = new ArrayList<>();

    public TimeTable(){
        for(int i = 0; i < 5; i++){
            days.add(new TimeTableDay());
        }
    }

    public void setDay(int day, TimeTableDay entry){
        days.set(day, entry);
    }

    public TimeTableDay getDay(int index){
        try{
            return days.get(index);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int getSize(){
        return days.size();
    }

    public class TimeTableDay implements Serializable{

        private static final long serialVersionUID = -7162123053432180842L;

        public ArrayList<TimeTableEntry> entries = new ArrayList<>();

        public void setEntry(int hour, TimeTableEntry entry){
            if(entries.size() <= hour)
                entries.add(entry);
            else
                entries.set(hour, entry);
        }

        public TimeTableEntry getEntry(int hour){
            return hour < entries.size() ? entries.get(hour) : null;
        }

        public void removeEntry(int hour){
            if(hour < entries.size())
                entries.remove(hour);
        }

        public int getSize(){
            return entries.size();
        }
    }

    public static class TimeTableEntry implements Serializable{

        private static final long serialVersionUID = -7162123053432181952L;

        String subjectA = "";
        String roomA = "";
        String teacherA = "";
        boolean emptyA = true;

        String subjectB = "";
        String roomB = "";
        String teacherB = "";
        boolean emptyB = true;

        public TimeTableEntry(String subjectA, String roomA, String teacherA, String subjectB, String roomB, String teacherB){
            this.subjectA = subjectA;
            this.roomA = roomA;
            this.teacherA = teacherA;

            this.subjectB = subjectB;
            this.roomB = roomB;
            this.teacherB = teacherB;
        }

        public void setEmptyA(boolean empty){
            this.emptyA = empty;
        }

        public void setEmptyB(boolean empty){
            this.emptyB = empty;
        }

        public String getSubjectA(){
            return subjectA;
        }

        public String getRoomA(){
            return roomA;
        }

        public String getTeacherA(){
            return teacherA;
        }

        public String getSubjectB(){
            return subjectB;
        }

        public String getRoomB(){
            return roomB;
        }

        public String getTeacherB(){
            return teacherB;
        }

        public boolean getEmptyA(){
            return emptyA;
        }

        public boolean getEmptyB(){
            return emptyB;
        }
    }

}
