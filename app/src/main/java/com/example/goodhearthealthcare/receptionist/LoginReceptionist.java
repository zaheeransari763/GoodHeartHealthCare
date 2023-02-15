package com.example.goodhearthealthcare.receptionist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodhearthealthcare.LoginActivity;
import com.example.goodhearthealthcare.MainActivity;
import com.example.goodhearthealthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginReceptionist extends AppCompatActivity {

    TextInputLayout UserID, Password;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_receptionist);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        UserID = findViewById(R.id.ReceptionistUserID);
        Password = findViewById(R.id.ReceptionistPassword);

        TextView LoginPatient = findViewById(R.id.PatientLoginText);
        Button receptionistLogin_button = findViewById(R.id.receptionistLogin_button);
        receptionistLogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginValidation();
            }
        });

        LoginPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PatientLogin = new Intent(LoginReceptionist.this, LoginActivity.class);
                startActivity(PatientLogin);
            }
        });
    }

    private void loginValidation() {
        String userIdString = UserID.getEditText().getText().toString().trim();
        String passwordString = Password.getEditText().getText().toString().trim();

        if (userIdString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(this, " User ID and Password cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(userIdString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(LoginReceptionist.this, ReceptionistDashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        loadingBar.dismiss();
                    } else {
                        String msg = task.getException().getMessage();
                        Toast.makeText(LoginReceptionist.this, msg, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent mainIntent = new Intent(LoginReceptionist.this, ReceptionistDashboard.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }
}