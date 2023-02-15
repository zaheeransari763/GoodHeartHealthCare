package com.example.goodhearthealthcare.receptionist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodhearthealthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddStaffActivity extends AppCompatActivity {

    Button addStaffBtn, clearFormBtn;
    DatabaseReference hospitalRef, doctorsRef;
    ProgressDialog loadingBar;
    Spinner addStaffRole;
    String hospIDStr, hospNameStr, hospAddressStr;
    LinearLayout spinnerDoctorRoleLay;
    TextInputLayout staffRoleTextField, doctorSpecSpinnerLay, staffGenderLay, staffQualificationLay,
            staffNameLay, staffAddressLay, staffPhoneLay, staffEmailLay, hospitalIDLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        hospIDStr = "GHHC0123";
        hospNameStr = "Good Heart Health Care";
        hospAddressStr = "Patel Compound, Dhamankar Naka, Bhiwandi - 421302";

        loadingBar = new ProgressDialog(this);
        hospitalRef = FirebaseDatabase.getInstance().getReference().child("Staffs");

        staffQualificationLay = findViewById(R.id.staffQualificationLay);
        hospitalIDLay = findViewById(R.id.hospitalIDLay);
        hospitalIDLay.getEditText().setText(hospIDStr);
        staffNameLay = findViewById(R.id.staffNameLay);
        staffAddressLay = findViewById(R.id.staffAddressLay);
        staffPhoneLay = findViewById(R.id.staffPhoneLay);
        staffEmailLay = findViewById(R.id.staffEmailLay);

        staffGenderLay = findViewById(R.id.staffGenderLay);
        staffRoleTextField = findViewById(R.id.staffRoleTextField);
        doctorSpecSpinnerLay = findViewById(R.id.doctorSpecSpinnerLay);

        spinnerDoctorRoleLay = findViewById(R.id.spinnerDoctorRoleLay);
        addStaffRole = findViewById(R.id.doctor_role_spinner);

        clearFormBtn = findViewById(R.id.clearFormBtn);
        clearFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFormFields();
            }
        });

        addStaffBtn = findViewById(R.id.addStaffBtn);
        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String staffName = staffNameLay.getEditText().getText().toString();
                String staffAddress = staffAddressLay.getEditText().getText().toString();
                String staffPhone = staffPhoneLay.getEditText().getText().toString();
                String staffEmail = staffEmailLay.getEditText().getText().toString();
                String staffQuali = staffQualificationLay.getEditText().getText().toString();
                String staffGender = staffGenderLay.getEditText().getText().toString();
                String staffRole = staffRoleTextField.getEditText().getText().toString();
                String doctorSpecial = doctorSpecSpinnerLay.getEditText().getText().toString();

                if (staffName.isEmpty() || staffAddress.isEmpty() || staffPhone.isEmpty() || staffEmail.isEmpty() ||
                        staffGender.isEmpty() || staffRole.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Some Field's are empty", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    HashMap<String, Object> staffMap = new HashMap<>();
                    staffMap.put("StaffName", staffName);
                    staffMap.put("StaffAddress", staffAddress);
                    staffMap.put("StaffPhone", staffPhone);
                    staffMap.put("StaffEmail", staffEmail);
                    staffMap.put("StaffQualification", staffQuali);
                    staffMap.put("StaffGender", staffGender);
                    staffMap.put("StaffRole", staffRole);
                    staffMap.put("DoctorSpecial", doctorSpecial);
                    staffMap.put("image", "default");
                    staffMap.put("Password", "dummy");
                    staffMap.put("HospitalID", hospIDStr);
                    staffMap.put("HospitalName", hospNameStr);
                    staffMap.put("HospitalAddress", hospAddressStr);
                    hospitalRef.child(staffRole).child(staffPhone).updateChildren(staffMap).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        clearFormFields();
                                        Toast.makeText(AddStaffActivity.this, staffRole + " added successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loadingBar.dismiss();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                    //doctorsRef.child(staffPhone).updateChildren(staffMap);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter4Specialist = ArrayAdapter.createFromResource(this, R.array.specialist, android.R.layout.simple_spinner_item);
        adapter4Specialist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addStaffRole.setAdapter(adapter4Specialist);
        addStaffRole.setOnItemSelectedListener(new SpecialistSpinner());
    }

    private void clearFormFields() {
        staffNameLay.getEditText().setText("");
        staffAddressLay.getEditText().setText("");
        staffPhoneLay.getEditText().setText("");
        staffEmailLay.getEditText().setText("");
        staffGenderLay.getEditText().setText("");
        staffRoleTextField.getEditText().setText("");
        staffNameLay.getEditText().setText("");
    }

    public void onRadioButtonCLicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.radioDoctor:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Doctor");
                    spinnerDoctorRoleLay.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radioCLerk:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Clerk");
                    spinnerDoctorRoleLay.setVisibility(View.GONE);
                    doctorSpecSpinnerLay.getEditText().setText("NA");
                }
                break;
            case R.id.radioNurse:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Nurse");
                    spinnerDoctorRoleLay.setVisibility(View.GONE);
                    doctorSpecSpinnerLay.getEditText().setText("NA");
                }
                break;
            case R.id.radioWardboy:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Wardboy");
                    spinnerDoctorRoleLay.setVisibility(View.GONE);
                    doctorSpecSpinnerLay.getEditText().setText("NA");
                }
                break;
            case R.id.radioReceptionist:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Receptionist");
                    spinnerDoctorRoleLay.setVisibility(View.GONE);
                    doctorSpecSpinnerLay.getEditText().setText("NA");
                }
                break;
            case R.id.radioWatchman:
                if (checked) {
                    staffRoleTextField.getEditText().setText("Watchman");
                    spinnerDoctorRoleLay.setVisibility(View.GONE);
                    doctorSpecSpinnerLay.getEditText().setText("NA");
                }
                break;
        }
    }

    public void onGenderRadioButtonCLicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioMale:
                if (checked)
                    staffGenderLay.getEditText().setText("Male");
                break;
            case R.id.radioFemale:
                if (checked)
                    staffGenderLay.getEditText().setText("Female");
                break;
        }
    }

    public class SpecialistSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String itemSpinner = parent.getItemAtPosition(position).toString();
            doctorSpecSpinnerLay.getEditText().setText(itemSpinner);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}