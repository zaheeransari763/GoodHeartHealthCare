package com.example.goodhearthealthcare.modal;

public class Staff {
    String DoctorSpecial, HospitalID, Password, StaffAddress, StaffEmail, StaffGender, StaffName, StaffPhone, StaffRole, image, HospitalAddress, HospitalName;

    public Staff() {
    }

    public Staff(String doctorSpecial, String hospitalID, String password, String staffAddress, String staffEmail, String staffGender, String staffName, String staffPhone, String staffRole, String image, String hospitalAddress, String hospitalName) {
        DoctorSpecial = doctorSpecial;
        HospitalID = hospitalID;
        Password = password;
        StaffAddress = staffAddress;
        StaffEmail = staffEmail;
        StaffGender = staffGender;
        StaffName = staffName;
        StaffPhone = staffPhone;
        StaffRole = staffRole;
        this.image = image;
        HospitalAddress = hospitalAddress;
        HospitalName = hospitalName;
    }

    public String getDoctorSpecial() {
        return DoctorSpecial;
    }

    public void setDoctorSpecial(String doctorSpecial) {
        DoctorSpecial = doctorSpecial;
    }

    public String getHospitalID() {
        return HospitalID;
    }

    public void setHospitalID(String hospitalID) {
        HospitalID = hospitalID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStaffAddress() {
        return StaffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        StaffAddress = staffAddress;
    }

    public String getStaffEmail() {
        return StaffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        StaffEmail = staffEmail;
    }

    public String getStaffGender() {
        return StaffGender;
    }

    public void setStaffGender(String staffGender) {
        StaffGender = staffGender;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String staffName) {
        StaffName = staffName;
    }

    public String getStaffPhone() {
        return StaffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        StaffPhone = staffPhone;
    }

    public String getStaffRole() {
        return StaffRole;
    }

    public void setStaffRole(String staffRole) {
        StaffRole = staffRole;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHospitalAddress() {
        return HospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        HospitalAddress = hospitalAddress;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }
}

