package com.example.bus_tracking_admin;

public class MainModel {
    String name, USN, complaint, date;

    MainModel(){

    }
    public MainModel(String name, String USN, String complaint, String date) {
        this.name = name;
        this.USN = USN;
        this.complaint = complaint;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUSN() {
        return USN;
    }

    public void setUSN(String USN) {
        this.USN = USN;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getDate(){ return date; }
}