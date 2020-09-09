package com.reminder.myapplication;

public class Reminder {
    private String ID;
    private String NAME;
    private String Date;

    public Reminder(String id, String name, String date) {
        ID = id;
        NAME = name;
        Date = date;

    }

    public String getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getDate() {
        return Date;
    }
}
