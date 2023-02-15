package com.example.goodhearthealthcare.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class RejectedLabTest extends Fragment {

    String nodeName = "LabTestRejected";

    String userID;
    RecyclerView viewLabTestReject;
    String doctorID;
    DatabaseReference patientRef, appointRejRef, appointConRef;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    TextView noLabTestRejectTxt;
    FirebaseStorage reportFirebaseStorage;
    StorageReference reportStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rejected_lab_test, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(getActivity());

        reportFirebaseStorage = FirebaseStorage.getInstance();
        reportStorageRef = reportFirebaseStorage.getReference().child("UploadReports");

        viewLabTestReject = view.findViewById(R.id.viewLabTestReject);
        noLabTestRejectTxt = view.findViewById(R.id.noLabTestRejectTxt);

        viewLabTestReject.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewLabTestReject.setLayoutManager(linearLayoutManager);

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
        patientRef.child(userID).child("LabTestRejected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("Patients").child(userID).child("LabTestRejected").limitToLast(50);
                    FirebaseRecyclerOptions<LabTest> options = new FirebaseRecyclerOptions.Builder<LabTest>().setQuery(query, LabTest.class).build();
                    FirebaseRecyclerAdapter<LabTest, viewLabTestRejViewHolder> adapter = new FirebaseRecyclerAdapter<LabTest, viewLabTestRejViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewLabTestRejViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final LabTest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getTestDate());
                            holder.setTime(model.getTestTime());
                            holder.setTestName(model.getTestName());
                            if (model.getTestStatus().equals("Booked")) {
                                holder.setTestStatus("Rejected");
                            }
                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewLabTestRejViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_lab_test_confirm_patient, parent, false);
                            return new viewLabTestRejViewHolder(view);
                        }
                    };
                    viewLabTestReject.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewLabTestReject.setVisibility(View.GONE);
                    noLabTestRejectTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static class viewLabTestRejViewHolder extends RecyclerView.ViewHolder {
        public viewLabTestRejViewHolder(@NonNull View itemView) {
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
}