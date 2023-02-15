package com.example.goodhearthealthcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class AddDiseasesActivity extends AppCompatActivity {

    LinearLayout linearDiabetic, linearPreviousMedication, linearAllergies, linearThyroid;
    Button diseaseProceedBtn;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference patientRef;
    String currUserId;
    TextInputLayout haveDiabetes, havePreviousMedi, haveAllergies, haveThyroid;
    TextInputEditText diabeticSince, diabeticBefore, diabeticAfter;
    TextInputEditText previousMedicationSince, previousMedicationCause;
    TextInputEditText allergiesSince, allergiesCause;
    TextInputEditText thyroidSince, thyroidMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diseases);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        currUserId = mAuth.getCurrentUser().getUid();
        patientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currUserId);

        haveDiabetes = findViewById(R.id.haveDiabetes);
        havePreviousMedi = findViewById(R.id.havePreviousMedi);
        haveAllergies = findViewById(R.id.haveAllergies);
        haveThyroid = findViewById(R.id.haveThyroid);

        linearDiabetic = findViewById(R.id.linearDiabetic);
        linearPreviousMedication = findViewById(R.id.linearPreviousMedication);
        linearAllergies = findViewById(R.id.linearAllergies);
        linearThyroid = findViewById(R.id.linearThyroid);

        diabeticSince = findViewById(R.id.diabeticSince);
        diabeticBefore = findViewById(R.id.diabeticBefore);
        diabeticAfter = findViewById(R.id.diabeticAfter);
        previousMedicationSince = findViewById(R.id.previousMedicationSince);
        previousMedicationCause = findViewById(R.id.previousMedicationCause);
        allergiesSince = findViewById(R.id.allergiesSince);
        allergiesCause = findViewById(R.id.allergiesCause);
        thyroidSince = findViewById(R.id.thyroidSince);
        thyroidMeasure = findViewById(R.id.thyroidMeasure);

        diseaseProceedBtn = findViewById(R.id.diseaseProceedBtn);
        diseaseProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hDia = haveDiabetes.getEditText().getText().toString();
                String hPreMed = havePreviousMedi.getEditText().getText().toString();
                String hAller = haveAllergies.getEditText().getText().toString();
                String hThy = haveThyroid.getEditText().getText().toString();
                String dSince = diabeticSince.getText().toString();
                String dBefore = diabeticBefore.getText().toString();
                String dAfter = diabeticAfter.getText().toString();
                String pMedSince = previousMedicationSince.getText().toString();
                String pMedCause = previousMedicationCause.getText().toString();
                String aSince = allergiesSince.getText().toString();
                String aCause = allergiesCause.getText().toString();
                String tSince = thyroidSince.getText().toString();
                String tMeasure = thyroidMeasure.getText().toString();

                if (hDia.isEmpty() || hPreMed.isEmpty() || hAller.isEmpty() || hThy.isEmpty()) {
                    Toast.makeText(AddDiseasesActivity.this, "Please select yes or no", Toast.LENGTH_SHORT).show();
                    if (hDia.equals("NO")) {
                        diabeticSince.setText("NA");
                        diabeticBefore.setText("NA");
                        diabeticAfter.setText("NA");
                    }
                    if (hPreMed.equals("NO")) {
                        previousMedicationSince.setText("NA");
                        previousMedicationCause.setText("NA");
                    }
                    if (hAller.equals("NO")) {
                        allergiesSince.setText("NA");
                        allergiesCause.setText("NA");
                    }
                    if (hThy.equals("NO")) {
                        thyroidSince.setText("NA");
                        thyroidMeasure.setText("NA");
                    }
                } else {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    HashMap diseasesMap = new HashMap();
                    diseasesMap.put("HaveDiabetes", hDia);
                    diseasesMap.put("HavePreMedication", hPreMed);
                    diseasesMap.put("HaveAllergies", hAller);
                    diseasesMap.put("HaveThyroid", hThy);
                    diseasesMap.put("DiabetesSince", dSince);
                    diseasesMap.put("DiabetesBefore", dBefore);
                    diseasesMap.put("DiabetesAfter", dAfter);
                    diseasesMap.put("PreMedSince", pMedSince);
                    diseasesMap.put("PreMedCause", pMedCause);
                    diseasesMap.put("AllergiesSince", aSince);
                    diseasesMap.put("AllergiesCause", aCause);
                    diseasesMap.put("ThyroidSince", tSince);
                    diseasesMap.put("ThyroidMeasure", tMeasure);
                    patientRef.child("OldMedi").updateChildren(diseasesMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AddDiseasesActivity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(AddDiseasesActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void onRadioButtonDiabeticCLicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.diabeticYes:
                if (checked)
                    linearDiabetic.setVisibility(View.VISIBLE);
                haveDiabetes.getEditText().setText("YES");
                break;
            case R.id.diabeticNo:
                if (checked)
                    linearDiabetic.setVisibility(View.GONE);
                haveDiabetes.getEditText().setText("NO");
                break;
        }
    }

    public void onRadioButtonPreMediCLicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.previousMedicationYes:
                if (checked)
                    linearPreviousMedication.setVisibility(View.VISIBLE);
                havePreviousMedi.getEditText().setText("YES");
                break;
            case R.id.previousMedicationNo:
                if (checked)
                    linearPreviousMedication.setVisibility(View.GONE);
                havePreviousMedi.getEditText().setText("NO");
                break;
        }
    }

    public void onRadioButtonAllergiesClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.allergiesYes:
                if (checked)
                    linearAllergies.setVisibility(View.VISIBLE);
                haveAllergies.getEditText().setText("YES");
                break;
            case R.id.allergiesNo:
                if (checked)
                    linearAllergies.setVisibility(View.GONE);
                haveAllergies.getEditText().setText("NO");
                break;
        }
    }

    public void onRadioButtonThyroidClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.thyroidYes:
                if (checked)
                    linearThyroid.setVisibility(View.VISIBLE);
                haveThyroid.getEditText().setText("YES");
                break;
            case R.id.thyroidNo:
                if (checked)
                    linearThyroid.setVisibility(View.GONE);
                haveThyroid.getEditText().setText("NO");
                break;
        }
    }
}