package com.example.goodhearthealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class phoneNumber extends AppCompatActivity {

    TextInputLayout phoneTextField;
    Button sendOtpBtn;
    //String phone, email, password, fName, lName, dob, age, address, city, district, pincode, gender, marital, altPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        phoneTextField = findViewById(R.id.phoneTextField);
        /*phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        fName = getIntent().getStringExtra("fName");
        lName = getIntent().getStringExtra("lName");
        dob = getIntent().getStringExtra("dob");
        age = getIntent().getStringExtra("age");
        address = getIntent().getStringExtra("address");
        gender = getIntent().getStringExtra("gender");
        marital = getIntent().getStringExtra("marital");
        city = getIntent().getStringExtra("city");
        district = getIntent().getStringExtra("district");
        pincode = getIntent().getStringExtra("pincode");
        altPhone = getIntent().getStringExtra("AltPhone");

        phoneTextField.getEditText().setText(phone);*/

        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = phoneTextField.getEditText().getText().toString();
                int len = num.length();
                if (num.isEmpty()){
                    Toast.makeText(phoneNumber.this, "Please provide phone number", Toast.LENGTH_SHORT).show();
                }
                else if (len!=10){
                    Toast.makeText(phoneNumber.this, "Phone number must contain exact 10 digits ", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Intent intent = new Intent(phoneNumber.this, OTPScreen.class);
                    intent.putExtra("phone",num);
                    /*intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    intent.putExtra("fName", fName);
                    intent.putExtra("lName", lName);
                    intent.putExtra("dob", dob);
                    intent.putExtra("age", age);
                    intent.putExtra("gender", gender);
                    intent.putExtra("marital", marital);
                    intent.putExtra("address", address);
                    intent.putExtra("city", city);
                    intent.putExtra("district", district);
                    intent.putExtra("pincode", pincode);
                    intent.putExtra("AltPhone", altPhone);*/
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}