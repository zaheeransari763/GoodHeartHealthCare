package com.example.goodhearthealthcare.receptionist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.goodhearthealthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddLabsActivity extends AppCompatActivity {

    TextInputLayout labNameLay, labAddressLay, labPhoneLay, labEmailLay;
    EditText selectFromTime, selectToTime;
    Button addLabBtn,clearFormBtn;
    DatabaseReference labRef;
    TextView tapFromDialog, tapToDialog;
    int fromHour, fromMinute, toHour, toMinute;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_labs);

        loadingBar = new ProgressDialog(this);

        labRef = FirebaseDatabase.getInstance().getReference().child("Labs");

        labNameLay = findViewById(R.id.labNameLay);
        labAddressLay = findViewById(R.id.labAddressLay);
        labPhoneLay = findViewById(R.id.labPhoneLay);
        labEmailLay = findViewById(R.id.labEmailLay);

        selectFromTime = findViewById(R.id.selectFromTime);
        selectToTime = findViewById(R.id.selectToTime);

        tapFromDialog = findViewById(R.id.tapFromDailog);
        selectFromTime = findViewById(R.id.selectFromTime);
        tapFromDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddLabsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                fromHour = hourOfDay;
                                fromMinute = minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, fromHour, fromMinute);
                                //selectFromTime.setText(DateFormat.format("hh:mm aa"));
                                android.text.format.DateFormat df = new android.text.format.DateFormat();
                                selectFromTime.setText(df.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(fromHour, fromMinute);
                timePickerDialog.show();
            }
        });

        tapToDialog = findViewById(R.id.tapToDailog);
        selectToTime = findViewById(R.id.selectToTime);
        tapToDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddLabsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                toHour = hourOfDay;
                                toMinute = minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, toHour, toMinute);
                                //selectFromTime.setText(DateFormat.format("hh:mm aa"));
                                android.text.format.DateFormat df = new android.text.format.DateFormat();
                                selectToTime.setText(df.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(toHour, toMinute);
                timePickerDialog.show();
            }
        });

        clearFormBtn = findViewById(R.id.clearFormBtn);
        clearFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFormFields();
            }
        });


        addLabBtn = findViewById(R.id.addLabBtn);
        addLabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = labNameLay.getEditText().getText().toString();
                String address = labAddressLay.getEditText().getText().toString();
                String phone = labPhoneLay.getEditText().getText().toString();
                String email = labEmailLay.getEditText().getText().toString();
                String toTime = selectToTime.getText().toString();
                String fromTime = selectFromTime.getText().toString();
                String nameStr = name.substring(0,3).toUpperCase();
                String phoneStr = phone.substring(0,7);
                String labID = nameStr+phoneStr;
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(toTime) || TextUtils.isEmpty(fromTime)){
                    Toast.makeText(getApplicationContext(), "Field's are empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setMessage("validating please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap hospitalMap = new HashMap();
                    hospitalMap.put("LabName",name.trim());
                    hospitalMap.put("LabAddress",address.trim());
                    hospitalMap.put("LabPhone",phone.trim());
                    hospitalMap.put("LabEmail",email.trim());
                    hospitalMap.put("ToTime",toTime.trim());
                    hospitalMap.put("FromTime",fromTime.trim());
                    hospitalMap.put("LabID",labID.trim());
                    hospitalMap.put("Password","dummy");
                    labRef.child(labID).updateChildren(hospitalMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Lab added", Toast.LENGTH_SHORT).show();
                                clearFormFields();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void clearFormFields() {
        labNameLay.getEditText().getText().clear();
        labAddressLay.getEditText().getText().clear();
        labPhoneLay.getEditText().getText().clear();
        labEmailLay.getEditText().getText().clear();
        selectFromTime.getText().clear();
        selectToTime.getText().clear();
    }
}