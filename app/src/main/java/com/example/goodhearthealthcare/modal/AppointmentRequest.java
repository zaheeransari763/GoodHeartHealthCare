package com.example.goodhearthealthcare.modal;

public class AppointmentRequest {
    String AppDate, AppTime, DoctorID, PatientAddress, PatientName, PatientPhone, PatientID, AppointmentID;

    public AppointmentRequest() {
    }

    public AppointmentRequest(String appDate, String appTime, String doctorID, String patientAddress, String patientName, String patientPhone, String patientID, String appointmentID) {
        AppDate = appDate;
        AppTime = appTime;
        DoctorID = doctorID;
        PatientAddress = patientAddress;
        PatientName = patientName;
        PatientPhone = patientPhone;
        PatientID = patientID;
        AppointmentID = appointmentID;
    }

    public String getAppDate() {
        return AppDate;
    }

    public void setAppDate(String appDate) {
        AppDate = appDate;
    }

    public String getAppTime() {
        return AppTime;
    }

    public void setAppTime(String appTime) {
        AppTime = appTime;
    }

    public String getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(String doctorID) {
        DoctorID = doctorID;
    }

    public String getPatientAddress() {
        return PatientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        PatientAddress = patientAddress;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getPatientPhone() {
        return PatientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        PatientPhone = patientPhone;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        PatientID = patientID;
    }

    public String getAppointmentID() {
        return AppointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        AppointmentID = appointmentID;
    }
}
