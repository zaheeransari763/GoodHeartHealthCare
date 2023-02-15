package com.example.goodhearthealthcare.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.goodhearthealthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class DoctorsProfileView extends Fragment {

    TextView bookAppointmentText, doctorName, doctorSpecialist, doctorEmail, selectDateText, selectTimeText;
    EditText selectAppointmentDate, selectAppointmentTime;
    LinearLayout appointmentLayout;
    TextInputLayout patientNameLay, patientPhoneLay, patientAddressLay;
    String doctorIDStr, patientID;
    DatabaseReference doctorsRef, patientRef;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    Button appointmentBtn;
    int fromHour, fromMinute;
    long today;
    DateTimeFormatter dtf;
    LocalDateTime now;
    String saveCurrentDate, saveCurrentTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctors_profile_view, container, false);

        loadingBar = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        patientID = mAuth.getCurrentUser().getUid();

        Calendar callForDate = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        final SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        //hospIDStr = getIntent().getExtras().get("hospID").toString();
        doctorIDStr = this.getArguments().getString("staffPhone");
        patientRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        doctorsRef = FirebaseDatabase.getInstance().getReference();

        appointmentLayout = view.findViewById(R.id.appointmentLayout);
        doctorName = view.findViewById(R.id.doctorName);
        doctorSpecialist = view.findViewById(R.id.doctorSpecialist);
        doctorEmail = view.findViewById(R.id.doctorEmail);

        patientNameLay = view.findViewById(R.id.patientNameLay);
        patientPhoneLay = view.findViewById(R.id.patientPhoneLay);
        patientAddressLay = view.findViewById(R.id.patientAddressLay);
        appointmentBtn = view.findViewById(R.id.appointmentBtn);

        bookAppointmentText = view.findViewById(R.id.bookAppointmentText);
        bookAppointmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentLayout.setVisibility(View.VISIBLE);
                bookAppointmentText.setVisibility(View.GONE);
            }
        });

        selectDateText = view.findViewById(R.id.selectDateText);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        today = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Preferred Date");
        builder.setSelection(today);
        final MaterialDatePicker materialDatePicker = builder.build();
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                selectAppointmentDate.setText(materialDatePicker.getHeaderText());
            }
        });

        selectTimeText = view.findViewById(R.id.selectTimeText);
        selectTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                fromHour = hourOfDay;
                                fromMinute = minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, fromHour, fromMinute);
                                //selectFromTime.setText(DateFormat.format("hh:mm aa"));
                                android.text.format.DateFormat df = new android.text.format.DateFormat();
                                selectAppointmentTime.setText(df.format("hh:mm:ss aa", calendar));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(fromHour, fromMinute);
                timePickerDialog.show();
            }
        });

        appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference doctorsAppRef = doctorsRef.child("AppointmentRequest");
                String patientName = patientNameLay.getEditText().getText().toString();
                String patientPhone = patientPhoneLay.getEditText().getText().toString();
                String patientAddress = patientAddressLay.getEditText().getText().toString();
                String appDate = selectAppointmentDate.getText().toString();
                String appTime = selectAppointmentTime.getText().toString();
                String appointmentID = patientID + "-" + saveCurrentDate + "-" + saveCurrentTime;
                if (patientName.isEmpty() || patientPhone.isEmpty() || patientAddress.isEmpty() || appDate.isEmpty() || appTime.isEmpty()) {
                    Toast.makeText(getActivity(), "Error: Field's are empty", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap appointmentReqMap = new HashMap();
                    appointmentReqMap.put("PatientName", patientName);
                    appointmentReqMap.put("PatientPhone", patientPhone);
                    appointmentReqMap.put("PatientAddress", patientAddress);
                    appointmentReqMap.put("AppTime", appTime);
                    appointmentReqMap.put("AppDate", appDate);
                    appointmentReqMap.put("DoctorID", doctorIDStr);
                    appointmentReqMap.put("DoctorName", doctorName.getText().toString());
                    appointmentReqMap.put("DoctorSpecialist", doctorSpecialist.getText().toString());
                    appointmentReqMap.put("PatientID", patientID);
                    appointmentReqMap.put("AppointmentID", appointmentID);
                    doctorsAppRef.child(appointmentID).updateChildren(appointmentReqMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Alert("Request for appointment is submitted successfully. Will update you once your Appointment get confirmed!");
                            }
                        }
                    });
                    patientRef.child(patientID).child("AppointmentRequest").child(appointmentID).updateChildren(appointmentReqMap);
                }
            }
        });

        selectAppointmentDate = view.findViewById(R.id.selectAppointmentDate);
        selectAppointmentTime = view.findViewById(R.id.selectAppointmentTime);

        loadingBar.setMessage("please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        patientRef.child(patientID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String pt_fName = snapshot.child("fName").getValue().toString();
                    String pt_lName = snapshot.child("lName").getValue().toString();
                    String pt_phone = snapshot.child("Phone").getValue().toString();
                    String pt_address = snapshot.child("Address").getValue().toString();
                    String name = pt_fName + " " + pt_lName;
                    patientNameLay.getEditText().setText(name);
                    patientPhoneLay.getEditText().setText(pt_phone);
                    patientAddressLay.getEditText().setText(pt_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        doctorsRef.child("Staffs").child("Doctor").child(doctorIDStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dr_name = snapshot.child("StaffName").getValue().toString();
                    String dr_spec = snapshot.child("DoctorSpecial").getValue().toString();
                    String dr_email = snapshot.child("StaffEmail").getValue().toString();
                    doctorName.setText(dr_name);
                    doctorSpecialist.setText(dr_spec);
                    doctorEmail.setText(dr_email);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    private void Alert(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Acknowledge")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        /*Intent intent = new Intent(getApplicationContext(), StudentLogin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                    }
                })
                .create();
        dialog.show();
    }
}