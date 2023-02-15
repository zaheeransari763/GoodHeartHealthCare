package com.example.goodhearthealthcare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.goodhearthealthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicalProfile extends Fragment {

    TextView patientMediAge, patientMediDiabetes, patientMediThyroid, patientMediAllergies;
    FirebaseAuth mAuth;
    DatabaseReference profileRef;
    String userId;

    LinearLayout linSugarDetails;
    TextView sugarSince, sugarBefore, sugarAfter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medical_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        profileRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(userId);

        linSugarDetails = view.findViewById(R.id.linSugarDetails);
        sugarSince = view.findViewById(R.id.sugarSince);
        sugarBefore = view.findViewById(R.id.sugarBefore);
        sugarAfter = view.findViewById(R.id.sugarAfter);

        patientMediAge = view.findViewById(R.id.patientMediAge);
        patientMediDiabetes = view.findViewById(R.id.patientMediDiabetes);
        patientMediThyroid = view.findViewById(R.id.patientMediThyroid);
        patientMediAllergies = view.findViewById(R.id.patientMediAllergies);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String age = snapshot.child("Age").getValue().toString();
                patientMediAge.setText(age);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileRef.child("OldMedi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hAllergies = snapshot.child("HaveAllergies").getValue().toString();
                String hDiabetes = snapshot.child("HaveDiabetes").getValue().toString();
                String hThyroid = snapshot.child("HaveThyroid").getValue().toString();
                String DiabetesSince = snapshot.child("DiabetesSince").getValue().toString();
                String DiabetesBefore = snapshot.child("DiabetesBefore").getValue().toString();
                String DiabetesAfter = snapshot.child("DiabetesAfter").getValue().toString();

                patientMediAllergies.setText(hAllergies);
                patientMediThyroid.setText(hThyroid);
                patientMediDiabetes.setText(hDiabetes);
                if (hDiabetes.equals("YES")) {
                    linSugarDetails.setVisibility(View.VISIBLE);
                    sugarSince.setText("Since: "+DiabetesSince);
                    sugarBefore.setText("Before: "+DiabetesBefore);
                    sugarAfter.setText("After: "+DiabetesAfter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return view;
    }
}