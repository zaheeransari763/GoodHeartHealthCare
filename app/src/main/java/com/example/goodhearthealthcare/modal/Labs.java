package com.example.goodhearthealthcare.modal;

public class Labs {
    String LabName, LabAddress, LabPhone, LabEmail,ToTime, FromTime, LabID, Password;

    public Labs() {
    }

    public Labs(String labName, String labAddress, String labPhone, String labEmail, String toTime, String fromTime, String labID, String password) {
        LabName = labName;
        LabAddress = labAddress;
        LabPhone = labPhone;
        LabEmail = labEmail;
        ToTime = toTime;
        FromTime = fromTime;
        LabID = labID;
        Password = password;
    }

    public String getLabName() {
        return LabName;
    }

    public void setLabName(String labName) {
        LabName = labName;
    }

    public String getLabAddress() {
        return LabAddress;
    }

    public void setLabAddress(String labAddress) {
        LabAddress = labAddress;
    }

    public String getLabPhone() {
        return LabPhone;
    }

    public void setLabPhone(String labPhone) {
        LabPhone = labPhone;
    }

    public String getLabEmail() {
        return LabEmail;
    }

    public void setLabEmail(String labEmail) {
        LabEmail = labEmail;
    }

    public String getToTime() {
        return ToTime;
    }

    public void setToTime(String toTime) {
        ToTime = toTime;
    }

    public String getFromTime() {
        return FromTime;
    }

    public void setFromTime(String fromTime) {
        FromTime = fromTime;
    }

    public String getLabID() {
        return LabID;
    }

    public void setLabID(String labID) {
        LabID = labID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
