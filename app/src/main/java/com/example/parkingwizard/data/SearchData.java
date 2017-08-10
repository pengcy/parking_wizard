package com.example.parkingwizard.data;

/**
 * Created by peng on 8/9/17.
 */

public class SearchData {

    private String zipCode;
    private String date;
    private String time;
    private int reserveTime;

    public SearchData(String zip, String date, String time, int reserveTime) {
        this.zipCode = zip;
        this.date = date;
        this.time = time;
        this.reserveTime = reserveTime;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(int reserveTime) {
        this.reserveTime = reserveTime;
    }


    public String toString() {
        return String.format("location=%1$s, date=%2$s, time=%3$s, reservationTime=%4$d", zipCode, date, time, reserveTime);
    }
}
