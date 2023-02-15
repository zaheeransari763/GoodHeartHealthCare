package com.example.goodhearthealthcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView signUpEditText, LoginReceptionist, forgotPassText;
    Button loginButton;
    TextInputLayout emailTextField, passTextField;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        emailTextField = findViewById(R.id.emailTextField);
        passTextField = findViewById(R.id.passTextField);


        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextField.getEditText().getText().toString().trim();
                String pass = passTextField.getEditText().getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Email or Password cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else  if (!email.contains("@gmail.com")){
                    Toast.makeText(LoginActivity.this, "Email is badly formatted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Please wait...");
                    loadingBar.setMessage("Logging in");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                loadingBar.dismiss();
                            }
                            else {
                                String msg = task.getException().getMessage();
                                if (msg.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                    Toast.makeText(LoginActivity.this, "No user Found", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                }


                                else if (msg.equals("The password is invalid or the user does not have a password.")){
                                        Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                }

                                else {
                                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                }

                            }
                        }
                    });
                }
            }
        });

        signUpEditText = findViewById(R.id.signupText);
        LoginReceptionist = findViewById(R.id.ReceptionistLoginText);
        forgotPassText = findViewById(R.id.forgotPassText);
        forgotPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        LoginReceptionist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ReceptionistScreen = new Intent(LoginActivity.this, com.example.goodhearthealthcare.receptionist.LoginReceptionist.class);
                startActivity(ReceptionistScreen);
            }
        });
        signUpEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent otpScreen = new Intent(LoginActivity.this, phoneNumber.class);
                startActivity(otpScreen);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }
}

