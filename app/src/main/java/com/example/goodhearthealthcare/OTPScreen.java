package com.example.goodhearthealthcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPScreen extends AppCompatActivity {

    Button OTP_button;
    //String userID, number, otpID, email, password, fName, lName, dob, age, address, city, district, pincode, gender, marital, altPhone;
    String number, otpID;
    TextView otpText;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    TextInputLayout otpTextField;
    DatabaseReference patientsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpscreen);

        patientsRef = FirebaseDatabase.getInstance().getReference().child("Patients");

        dialog = new ProgressDialog(this);

        number = getIntent().getStringExtra("phone");
        /*email = getIntent().getStringExtra("email");
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
        altPhone = getIntent().getStringExtra("AltPhone");*/
        mAuth = FirebaseAuth.getInstance();

        otpTextField = findViewById(R.id.otpTextField);

        otpText = findViewById(R.id.otpText);
        otpText.setText("OTP sent to +91-" + number);

        initiateOTP();

        OTP_button = findViewById(R.id.OTP_button);
        OTP_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpTextField.getEditText().getText().toString();
                if (otp.isEmpty()) {
                    Toast.makeText(OTPScreen.this, "OTP is empty", Toast.LENGTH_SHORT).show();
                } else {
                    PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(otpID, otp);
                    signInWithPhoneAuthCredential(authCredential);
                }
                /*Toast.makeText(getApplicationContext(), "OTP Verified ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SignupForm.class);
                intent.putExtra("phone",number);
                startActivity(intent);*/
            }
        });
    }

    private void initiateOTP() {
        dialog.setTitle("Please wait...");
        dialog.setMessage("Sending OTP. Please wait while we auto detect.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                                dialog.dismiss();
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                dialog.dismiss();
                                otpID = verificationId;
                                //mResendToken = token;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "OTP Verified ", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), SignupForm.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    dialog.setTitle("Please wait...");
                    dialog.setMessage("Verifying...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    String msg = task.getException().getMessage();
                    if (msg.equals("The sms code has expired. Please re-send the verification code to try again.")) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Code Expired", Toast.LENGTH_LONG).show();
                    } else if (msg.equals("The sms verification code used to create the phone auth credential is invalid. Please resend the verification code sms and be sure use the verification code provided by the user.")) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Invalid Code", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}