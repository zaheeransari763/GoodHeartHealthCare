package com.example.goodhearthealthcare.receptionist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.AppointmentRequest;
import com.example.goodhearthealthcare.modal.LabTest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ViewLabRequests extends AppCompatActivity {

    RecyclerView viewLabsTestRequest;
    String doctorID;
    DatabaseReference labTestReqRef, labTestRejRef,labTestConRef;
    ProgressDialog loadingBar;
    TextView noLabTestReqTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lab_requests);

        loadingBar = new ProgressDialog(this);

        noLabTestReqTxt = findViewById(R.id.noLabTestReqTxt);

        viewLabsTestRequest = findViewById(R.id.viewLabsTestRequest);
        viewLabsTestRequest.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        viewLabsTestRequest.setLayoutManager(linearLayoutManager);

        labTestReqRef = FirebaseDatabase.getInstance().getReference();
        labTestRejRef = FirebaseDatabase.getInstance().getReference();
        labTestConRef = FirebaseDatabase.getInstance().getReference();

        startListen();

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.setMessage("please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        startListen();
    }

    private void startListen() {
        labTestReqRef.child("LabTestReq").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Query query = FirebaseDatabase.getInstance().getReference().child("LabTestReq").limitToLast(50);
                    FirebaseRecyclerOptions<LabTest> options = new FirebaseRecyclerOptions.Builder<LabTest>().setQuery(query, LabTest.class).build();
                    FirebaseRecyclerAdapter<LabTest, viewLabsTestReqViewHolder> adapter = new FirebaseRecyclerAdapter<LabTest, viewLabsTestReqViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewLabsTestReqViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final LabTest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getTestDate());
                            holder.setTime(model.getTestTime());

                            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = model.getPatientName();
                                    String phone = model.getPatientPhone();
                                    String address = model.getPatientAddress();
                                    String date = model.getTestDate();
                                    String time = model.getTestTime();
                                    String patientID = model.getPatientID();
                                    String testID = model.getLabTestID();
                                    String testName = model.getTestName();
                                    HashMap<String, Object> patientMap = new HashMap<String, Object>();

                                    patientMap.put("PatientName",name);
                                    patientMap.put("PatientPhone",phone);
                                    patientMap.put("PatientAddress",address);
                                    patientMap.put("TestName",testName);
                                    patientMap.put("TestDate",date);
                                    patientMap.put("TestTime",time);
                                    patientMap.put("PatientID",patientID);
                                    patientMap.put("LabTestID",testID);
                                    patientMap.put("TestStatus","Booked");

                                    labTestConRef.child("Patients").child(patientID).child("LabTestConfirmed").child(testID).updateChildren(patientMap);
                                    labTestReqRef.child("Patients").child(patientID).child("LabTestRequest").child(testID).removeValue();
                                    labTestReqRef.child("LabTestReq").child(testID).removeValue();
                                    labTestConRef.child("LabTestConfirmed").child(testID).updateChildren(patientMap);
                                    Toast.makeText(getApplicationContext(), "LabTest Confirmed", Toast.LENGTH_SHORT).show();
                                }
                            });

                            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = model.getPatientName();
                                    String phone = model.getPatientPhone();
                                    String address = model.getPatientAddress();
                                    String date = model.getTestDate();
                                    String time = model.getTestTime();
                                    String patientID = model.getPatientID();
                                    String labTestIDRej = model.getLabTestID();
                                    String testName = model.getTestName();
                                    HashMap<String, Object> patientMap = new HashMap<String, Object>();
                                    patientMap.put("PatientName",name);
                                    patientMap.put("PatientPhone",phone);
                                    patientMap.put("PatientAddress",address);
                                    patientMap.put("TestName",testName);
                                    patientMap.put("TestDate",date);
                                    patientMap.put("TestTime",time);
                                    patientMap.put("PatientID",patientID);
                                    patientMap.put("LabTestID",labTestIDRej);
                                    patientMap.put("TestStatus","Booked");

                                    labTestConRef.child("Patients").child(patientID).child("LabTestRejected").child(labTestIDRej).updateChildren(patientMap);
                                    labTestReqRef.child("Patients").child(patientID).child("LabTestRequest").child(labTestIDRej).removeValue();
                                    labTestReqRef.child("LabTestReq").child(labTestIDRej).removeValue();
                                    labTestConRef.child("LabTestRejected").child(labTestIDRej).updateChildren(patientMap);

                                    //appointRejRef.child("Patients").child(patientID).child("AppointmentRejected").child(appointmentIDRej).updateChildren(patientMap);
                                    //appointRejRef.child("Patients").child(patientID).child("AppointmentRequest").child(appointmentIDRej).removeValue();
                                    //appointRejRef.child("AppointmentRequest").child(appointmentIDRej).removeValue();
                                    //appointConRef.child("AppointmentRejected").child(appointmentIDRej).updateChildren(patientMap);
                                    Toast.makeText(getApplicationContext(), "Appointment Rejected", Toast.LENGTH_SHORT).show();
                                }
                            });
                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewLabsTestReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_appointment_req, parent, false);
                            return new viewLabsTestReqViewHolder(view);
                        }
                    };
                    viewLabsTestRequest.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewLabsTestRequest.setVisibility(View.GONE);
                    noLabTestReqTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class viewLabsTestReqViewHolder extends RecyclerView.ViewHolder {
        Button acceptBtn, rejectBtn;
        public viewLabsTestReqViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView = itemView;
            acceptBtn = itemView.findViewById(R.id.appRqstAcceptBtn);
            rejectBtn = itemView.findViewById(R.id.appRqstRejectBtn);
        }

        public void setName(String fname) {
            TextView firstname = (TextView) itemView.findViewById(R.id.patientName);
            firstname.setText("Name: "+fname);
        }

        public void setPhone(String phone) {
            TextView phoneText = (TextView) itemView.findViewById(R.id.patientPhoneApt);
            phoneText.setText("Phone: "+phone);
        }

        public void setAddress(String address) {
            TextView setAddress = (TextView) itemView.findViewById(R.id.patientAddress);
            setAddress.setText("Address: "+address);
        }

        public void setDate(String date) {
            TextView dateTxt = (TextView) itemView.findViewById(R.id.appointmentDate);
            dateTxt.setText("Date: "+date);
        }

        public void setTime(String time) {
            TextView timeTxt = (TextView) itemView.findViewById(R.id.appointmentTime);
            timeTxt.setText("Time: "+time);
        }
        /*public void setImagee(Context ctx, String image)
        {
            CircleImageView donorimage = (CircleImageView) mView.findViewById(R.id.donor_profile_image);
            Picasso.with(ctx).load(image).into(donorimage);
        }*/
    }
}