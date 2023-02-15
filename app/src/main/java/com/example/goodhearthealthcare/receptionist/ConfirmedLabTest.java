package com.example.goodhearthealthcare.receptionist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.LabTest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmedLabTest extends AppCompatActivity {

    String userID, uploadReportId;
    ;
    RecyclerView viewConfirmedLabsTest;
    TextView noConfirmLabTestTxt;
    DatabaseReference labTestRef, patientRef;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    int PDF_CODE = 1;
    Uri pdfUri;
    Dialog uploadReportDialog;
    PDFView uploadPdfView;
    StorageReference uploadPdfStorageRef;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_lab_test);

        mAuth = FirebaseAuth.getInstance();
        //userID = mAuth.getCurrentUser().getUid();

        loadingBar = new ProgressDialog(this);
        uploadReportDialog = new Dialog(this);

        uploadPdfStorageRef = FirebaseStorage.getInstance().getReference().child("UploadReports");

        noConfirmLabTestTxt = findViewById(R.id.noConfirmLabTestTxt);
        viewConfirmedLabsTest = findViewById(R.id.viewConfirmedLabsTest);

        viewConfirmedLabsTest.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        viewConfirmedLabsTest.setLayoutManager(linearLayoutManager);

        labTestRef = FirebaseDatabase.getInstance().getReference().child("LabTestConfirmed");
        patientRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        startListen();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingBar.setMessage("please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        startListen();
    }

    private void startListen() {
        labTestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("LabTestConfirmed").limitToLast(50);
                    FirebaseRecyclerOptions<LabTest> options = new FirebaseRecyclerOptions.Builder<LabTest>().setQuery(query, LabTest.class).build();
                    FirebaseRecyclerAdapter<LabTest, viewConfirmedLabsTestViewHolder> adapter = new FirebaseRecyclerAdapter<LabTest, viewConfirmedLabsTestViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewConfirmedLabsTestViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final LabTest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getTestDate());
                            holder.setTime(model.getTestTime());
                            holder.setTestName(model.getTestName());
                            holder.setTestStatus(model.getTestStatus());
                            if (model.getTestStatus().toString().equals("Completed")) {
                                holder.itemView.findViewById(R.id.uploadTestReportImg).setVisibility(View.VISIBLE);
                            }
                            loadingBar.dismiss();

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Sample Collected",
                                                    "Reports Done"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmedLabTest.this);
                                    builder.setTitle("What is the status of the Test?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            if (i == 0) {
                                                UpdateStatusToSampleCollected(model.getPatientID(), model.getLabTestID());
                                            } else {
                                                UpdateStatusToReportsDone(model.getPatientID(), model.getLabTestID());
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            holder.itemView.findViewById(R.id.uploadTestReportImg).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    uploadReportDialog.setContentView(R.layout.upload_report_layout);

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(uploadReportDialog.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    uploadReportDialog.getWindow().setAttributes(lp);

                                    TextView selectPdfFile = uploadReportDialog.findViewById(R.id.selectPdfFile);
                                    selectPdfFile.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ContextCompat.checkSelfPermission(ConfirmedLabTest.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                SelectFileFromStorage();
                                            } else {
                                                ActivityCompat.requestPermissions(ConfirmedLabTest.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                                            }
                                        }
                                    });

                                    TextView uploadPdfFile = uploadReportDialog.findViewById(R.id.uploadPdfFile);
                                    uploadPdfFile.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (pdfUri != null) {
                                                //UploadPdfToFirebaseStorage(pdfUri);
                                                loadingBar.setMessage("please wait...");
                                                loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                //loadingBar.setProgress(0);
                                                loadingBar.setCanceledOnTouchOutside(true);
                                                loadingBar.show();

                                                final String saveCurrentTime, saveCurrentDate;
                                                Calendar calForDate = Calendar.getInstance();
                                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                                                saveCurrentDate = currentDate.format(calForDate.getTime());

                                                Calendar calForTime = Calendar.getInstance();
                                                SimpleDateFormat currentTime = new SimpleDateFormat("HH-MM-ss");
                                                saveCurrentTime = currentTime.format(calForTime.getTime());

                                                uploadReportId = saveCurrentDate + saveCurrentTime;

                                                final StorageReference fileReference = uploadPdfStorageRef.child(getFileName(pdfUri, getApplicationContext()));
                                                //uploadPdfStorageRef.child(getFileName(pdfUri)).putFile(pdfUri);
                                                uploadTask = fileReference.putFile(pdfUri);
                                                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        return fileReference.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri downloadUri = task.getResult();
                                                            String mUri = downloadUri.toString();
                                                            //String current_uid = mAuth.getUid();
                                                            HashMap<String, Object> reportMap = new HashMap<>();
                                                            reportMap.put("ReportsPdf", mUri);
                                                            reportMap.put("Date", saveCurrentDate);
                                                            reportMap.put("Time", saveCurrentTime);
                                                            reportMap.put("BookId", uploadReportId);
                                                            patientRef.child(model.getPatientID()).child("LabTestConfirmed").child(model.getLabTestID()).child("Reports").updateChildren(reportMap);
                                                            labTestRef.child(model.getLabTestID()).child("Reports").updateChildren(reportMap);

                                                            HashMap map = new HashMap();
                                                            map.put("TestStatus", "Reports Submitted");
                                                            patientRef.child(model.getPatientID()).child("LabTestConfirmed").child(model.getLabTestID()).updateChildren(map);
                                                            labTestRef.child(model.getLabTestID()).updateChildren(map);
                                                            loadingBar.dismiss();
                                                            uploadReportDialog.dismiss();
                                                        } else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(getApplicationContext(), "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(getApplicationContext(), "Error Occurred!" + exception, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ConfirmedLabTest.this, "please select file", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    uploadReportDialog.show();
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public viewConfirmedLabsTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_lab_test_confirm, parent, false);
                            return new viewConfirmedLabsTestViewHolder(view);
                        }
                    };
                    viewConfirmedLabsTest.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewConfirmedLabsTest.setVisibility(View.GONE);
                    noConfirmLabTestTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void UpdateStatusToReportsDone(String patientID, String labTestID) {
        HashMap map = new HashMap();
        map.put("TestStatus", "Completed");
        patientRef.child(patientID).child("LabTestConfirmed").child(labTestID).updateChildren(map);
        labTestRef.child(labTestID).updateChildren(map);
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
    }

    private void UpdateStatusToSampleCollected(String patientID, String labTestID) {
        HashMap map = new HashMap();
        map.put("TestStatus", "Collected");
        patientRef.child(patientID).child("LabTestConfirmed").child(labTestID).updateChildren(map);
        labTestRef.child(labTestID).updateChildren(map);
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
    }

    public class viewConfirmedLabsTestViewHolder extends RecyclerView.ViewHolder {
        public viewConfirmedLabsTestViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView = itemView;
            ImageView uploadTestReportImg = itemView.findViewById(R.id.uploadTestReportImg);
            uploadTestReportImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(itemView.getContext(), "Upload reports", Toast.LENGTH_SHORT).show();
                    /*uploadReportDialog.setContentView(R.layout.upload_report_layout);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(uploadReportDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    uploadReportDialog.getWindow().setAttributes(lp);

                    TextView selectPdfFile = uploadReportDialog.findViewById(R.id.selectPdfFile);
                    selectPdfFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ContextCompat.checkSelfPermission(ConfirmedLabTest.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                SelectFileFromStorage();
                            } else {
                                ActivityCompat.requestPermissions(ConfirmedLabTest.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                            }
                        }
                    });

                    TextView uploadPdfFile = uploadReportDialog.findViewById(R.id.uploadPdfFile);
                    uploadPdfFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (pdfUri != null)
                            {
                                UploadPdfToFirebaseStorage(pdfUri);
                            }
                            else
                            {
                                Toast.makeText(ConfirmedLabTest.this, "please select file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    uploadReportDialog.show();*/
                }
            });
        }

        public void setName(String fname) {
            TextView firstname = (TextView) itemView.findViewById(R.id.patientName);
            firstname.setText("Name: " + fname);
        }

        public void setPhone(String phone) {
            TextView phoneText = (TextView) itemView.findViewById(R.id.patientPhoneApt);
            phoneText.setText("Phone: " + phone);
        }

        public void setAddress(String address) {
            TextView setAddress = (TextView) itemView.findViewById(R.id.patientAddress);
            setAddress.setText("Address: " + address);
        }

        public void setDate(String date) {
            TextView dateTxt = (TextView) itemView.findViewById(R.id.appointmentDate);
            dateTxt.setText("Date: " + date);
        }

        public void setTime(String time) {
            TextView timeTxt = (TextView) itemView.findViewById(R.id.appointmentTime);
            timeTxt.setText("Time: " + time);
        }

        public void setTestName(String tName) {
            TextView tNameTxt = (TextView) itemView.findViewById(R.id.labTestName);
            tNameTxt.setText("Test Name: " + tName);
        }

        public void setTestStatus(String status) {
            TextView tStatus = itemView.findViewById(R.id.labTestStatus);
            tStatus.setText(status);
        }
        /*public void setImagee(Context ctx, String image)
        {
            CircleImageView donorimage = (CircleImageView) mView.findViewById(R.id.donor_profile_image);
            Picasso.with(ctx).load(image).into(donorimage);
        }*/
    }

    private void SelectFileFromStorage() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PDF_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_CODE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            TextView selectFileName = uploadReportDialog.findViewById(R.id.selectFileName);
            MaterialCardView uploadPdfFileCard = uploadReportDialog.findViewById(R.id.uploadPdfFileCard);
            uploadPdfFileCard.setVisibility(View.VISIBLE);
            uploadPdfView = uploadReportDialog.findViewById(R.id.uploadPdfView);

            selectFileName.setText("You selected:- " + getFileName(pdfUri, getApplicationContext()));
            //uploadPdfView.fromAsset("client.pdf").load();
            uploadPdfView.fromUri(pdfUri).load();
        } else {
            Toast.makeText(this, "Please select file", Toast.LENGTH_LONG).show();
        }
    }

    String getFileName(Uri uri, Context context) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null) {
                res = uri.getPath();
                int cutt = res.lastIndexOf('/');
                if (cutt != -1) {
                    res = res.substring(cutt + 1);
                }
            }
        }
        return res;
    }
}