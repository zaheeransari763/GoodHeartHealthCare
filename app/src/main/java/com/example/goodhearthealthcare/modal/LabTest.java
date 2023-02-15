package com.example.goodhearthealthcare.modal;

public class LabTest {
    String LabTestID, PatientAddress, PatientID, PatientName, PatientPhone, TestDate, TestName, TestStatus, TestTime;

    public LabTest() {
    }

    public LabTest(String labTestID, String patientAddress, String patientID, String patientName, String patientPhone, String testDate, String testName, String testStatus, String testTime) {
        LabTestID = labTestID;
        PatientAddress = patientAddress;
        PatientID = patientID;
        PatientName = patientName;
        PatientPhone = patientPhone;
        TestDate = testDate;
        TestName = testName;
        TestStatus = testStatus;
        TestTime = testTime;
    }

    public String getLabTestID() {
        return LabTestID;
    }

    public void setLabTestID(String labTestID) {
        LabTestID = labTestID;
    }

    public String getPatientAddress() {
        return PatientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        PatientAddress = patientAddress;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        PatientID = patientID;
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

    public String getTestDate() {
        return TestDate;
    }

    public void setTestDate(String testDate) {
        TestDate = testDate;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getTestStatus() {
        return TestStatus;
    }

    public void setTestStatus(String testStatus) {
        TestStatus = testStatus;
    }

    public String getTestTime() {
        return TestTime;
    }

    public void setTestTime(String testTime) {
        TestTime = testTime;
    }
}
