package com.example.goodhearthealthcare.modal;

public class MedicineReminder {
    String name, time;

    public MedicineReminder() {
    }

    public MedicineReminder(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
