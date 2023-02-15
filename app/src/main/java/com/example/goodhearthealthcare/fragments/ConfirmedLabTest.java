package com.example.goodhearthealthcare.fragments;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.LabTest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfirmedLabTest extends Fragment {

    String userID;
    RecyclerView viewLabTestRequest;
    String doctorID;
    DatabaseReference patientRef, appointRejRef, appointConRef;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    TextView noLabTestAppliedTxt;
    FirebaseStorage reportFirebaseStorage;
    StorageReference reportStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmed_lab_test, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(getActivity());

        reportFirebaseStorage = FirebaseStorage.getInstance();
        reportStorageRef = reportFirebaseStorage.getReference().child("UploadReports");

        noLabTestAppliedTxt = view.findViewById(R.id.noLabTestAppliedTxt);
        viewLabTestRequest = view.findViewById(R.id.viewLabTestRequest);

        viewLabTestRequest.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewLabTestRequest.setLayoutManager(linearLayoutManager);

        patientRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        startListen();
        return view;
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
        patientRef.child(userID).child("LabTestConfirmed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("Patients").child(userID).child("LabTestConfirmed").limitToLast(50);
                    FirebaseRecyclerOptions<LabTest> options = new FirebaseRecyclerOptions.Builder<LabTest>().setQuery(query, LabTest.class).build();
                    FirebaseRecyclerAdapter<LabTest, viewLabTestConViewHolder> adapter = new FirebaseRecyclerAdapter<LabTest, viewLabTestConViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewLabTestConViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final LabTest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getTestDate());
                            holder.setTime(model.getTestTime());
                            holder.setTestName(model.getTestName());
                            holder.setTestStatus(model.getTestStatus());
                            loadingBar.dismiss();
                            if (model.getTestStatus().toString().equals("Reports Submitted")) {
                                holder.itemView.findViewById(R.id.downloadTestReportImg).setVisibility(View.VISIBLE);
                            }
                            DatabaseReference repRef = patientRef.child(userID).child("LabTestConfirmed").child(model.getLabTestID()).child("Reports");
                            holder.itemView.findViewById(R.id.downloadTestReportImg).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    repRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String url = snapshot.child("ReportsPdf").getValue().toString();
                                            downloadFile(url);
                                            //downloadBookFunction(getContext(), userID, ".pdf", DIRECTORY_DOWNLOADS, url);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    //Toast.makeText(getContext(), "Logic yet to implement...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public viewLabTestConViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_lab_test_confirm_patient, parent, false);
                            return new viewLabTestConViewHolder(view);
                        }
                    };
                    viewLabTestRequest.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewLabTestRequest.setVisibility(View.GONE);
                    noLabTestAppliedTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static class viewLabTestConViewHolder extends RecyclerView.ViewHolder {
        public viewLabTestConViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView = itemView;
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
    }

    private void downloadFile(String repName) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Downloading report. please wait for few moment.");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        downloadBookFunction(getContext(), userID, ".pdf", DIRECTORY_DOWNLOADS, repName);
    }

    private void downloadBookFunction(Context context, String fileName, String extension, String destinationDir, String url) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(destinationDir, "Health Heart/" + "Reports/" + fileName + extension);
        manager.enqueue(request);
    }
}