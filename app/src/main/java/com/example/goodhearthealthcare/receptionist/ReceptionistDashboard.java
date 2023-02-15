package com.example.goodhearthealthcare.receptionist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.goodhearthealthcare.LoginActivity;
import com.example.goodhearthealthcare.MainActivity;
import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.fragments.ViewConfirmedAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReceptionistDashboard extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_dashboard);

        mAuth = FirebaseAuth.getInstance();
    }

    public void addLabs(View view) {
        Intent intent = new Intent(this, AddLabsActivity.class);
        startActivity(intent);
    }

    public void viewLabs(View view) {
        Intent intent = new Intent(this, ViewLabsActivity.class);
        startActivity(intent);
    }

    public void addHospitalStaff(View view) {
        Intent intent = new Intent(this, AddStaffActivity.class);
        startActivity(intent);
    }

    public void viewHospitalStaff(View view) {
        Intent intent = new Intent(this, ViewStaffsActivity.class);
        startActivity(intent);
    }

    public void viewAppointments(View view) {
        Intent intent = new Intent(this, ViewAppointmentReqs.class);
        startActivity(intent);
    }

    public void viewLabRequests(View view) {
        Intent intent = new Intent(this, ViewLabRequests.class);
        startActivity(intent);
    }

    public void viewConLabTests(View view) {
        Intent intent = new Intent(this, ConfirmedLabTest.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        mAuth.signOut();
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // User is signed in
            Intent mainIntent = new Intent(ReceptionistDashboard.this,LoginReceptionist.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }
}