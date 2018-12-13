package com.example.rizvanr.eps;

import java.util.ArrayList;

public class DB_Data {

    // dbData = YYYYMMDDHHMM-val (12 digits - value)
    public ArrayList<String> dbData;
    public ArrayList<Integer> hours;
    public ArrayList<Integer>  mins;
    public ArrayList<Integer>  scalableTime;
    public ArrayList<Double>  alcoLevel;
    public ArrayList<Long> date;
    public int numElements;

    public DB_Data(){
        this.mins = new ArrayList<Integer>();
        this.hours = new ArrayList<Integer>();
        this.scalableTime = new ArrayList<Integer>();
        this.alcoLevel = new ArrayList<Double>();
    }

    public Double getValue(String s){
        return Double.parseDouble(s.substring(s.lastIndexOf('-') + 1));
        //String temp = s.substring(12,15); ?
        //return Double.parseDouble(temp);
    }

    public Integer getHour(String s) {
        return Integer.parseInt(s.substring(7,9));
    }

    public Integer getMins(String s) {
        return Integer.parseInt(s.substring(9,11));
    }

    public Integer getScalableTime(String s) {
        return (this.getHour(s)*60+this.getMins(s));
    }

    public Long getDate(String s) {
        return Long.parseLong(s.substring(0,7));
    }

    public Integer getDay(String s) {
        return Integer.parseInt(s.substring(5,7));
    }

    public Integer getMonth(String s) {
        return Integer.parseInt(s.substring(3,5));
    }

}
