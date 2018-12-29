package com.example.rizvanr.eps;

public class FirebaseData {

    Long date_time;
    Long level;
    String tag;

    public FirebaseData(){}

    public FirebaseData(Long date_time, Long level, String tag) {
        this.date_time = date_time;
        this.level = level;
        this.tag = tag;
    }

    public String getDate_time() {
        return String.format("%12d",date_time);
    }

    public String getLevel() {
        return String.format("%3d",level);
    }

    public String getTag() {
        return tag;
    }
}
