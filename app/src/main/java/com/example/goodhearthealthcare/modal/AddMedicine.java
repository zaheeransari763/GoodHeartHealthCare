package com.example.goodhearthealthcare.modal;

import java.io.Serializable;

public class AddMedicine implements Serializable {
    String medName, medTime;

    public AddMedicine() {
    }

    public AddMedicine(String medName, String medTime) {
        this.medName = medName;
        this.medTime = medTime;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedTime() {
        return medTime;
    }

    public void setMedTime(String medTime) {
        this.medTime = medTime;
    }
}