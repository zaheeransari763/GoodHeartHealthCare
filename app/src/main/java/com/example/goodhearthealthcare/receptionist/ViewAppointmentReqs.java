package com.example.goodhearthealthcare.receptionist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.AppointmentRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ViewAppointmentReqs extends AppCompatActivity {

    RecyclerView viewAppointmentRequest;
    String doctorID;
    DatabaseReference appointmentReqRef, appointRejRef,appointConRef;
    ProgressDialog loadingBar;
    TextView noAppointmentReqTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_reqs);

        loadingBar = new ProgressDialog(this);

        noAppointmentReqTxt = findViewById(R.id.noAppointmentReqTxt);

        viewAppointmentRequest = findViewById(R.id.viewAppointmentRequest);
        viewAppointmentRequest.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        viewAppointmentRequest.setLayoutManager(linearLayoutManager);

        appointmentReqRef = FirebaseDatabase.getInstance().getReference();
        appointRejRef = FirebaseDatabase.getInstance().getReference();
        appointConRef = FirebaseDatabase.getInstance().getReference();

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
        appointmentReqRef.child("AppointmentRequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Query query = FirebaseDatabase.getInstance().getReference().child("AppointmentRequest").limitToLast(50);
                    FirebaseRecyclerOptions<AppointmentRequest> options = new FirebaseRecyclerOptions.Builder<AppointmentRequest>().setQuery(query, AppointmentRequest.class).build();
                    FirebaseRecyclerAdapter<AppointmentRequest, viewAppointmentReqViewHolder> adapter = new FirebaseRecyclerAdapter<AppointmentRequest, viewAppointmentReqViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewAppointmentReqViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final AppointmentRequest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getAppDate());
                            holder.setTime(model.getAppTime());

                            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = model.getPatientName();
                                    String phone = model.getPatientPhone();
                                    String address = model.getPatientAddress();
                                    String date = model.getAppDate();
                                    String time = model.getAppTime();
                                    String patientID = model.getPatientID();
                                    String appointmentID = model.getAppointmentID();
                                    HashMap<String, Object> patientMap = new HashMap<String, Object>();
                                    patientMap.put("PatientName", name);
                                    patientMap.put("PatientPhone", phone);
                                    patientMap.put("PatientID", patientID);
                                    patientMap.put("PatientAddress", address);
                                    patientMap.put("AppDate", date);
                                    patientMap.put("AppTime", time);
                                    patientMap.put("AppointmentID", appointmentID);
                                    appointConRef.child("Patients").child(patientID).child("AppointmentConfirmed").child(appointmentID).updateChildren(patientMap);
                                    appointRejRef.child("Patients").child(patientID).child("AppointmentRequest").child(appointmentID).removeValue();
                                    appointRejRef.child("AppointmentRequest").child(appointmentID).removeValue();
                                    appointConRef.child("AppointmentConfirmed").child(appointmentID).updateChildren(patientMap);
                                    Toast.makeText(getApplicationContext(), "Appointment Confirmed", Toast.LENGTH_SHORT).show();
                                }
                            });

                            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = model.getPatientName();
                                    String phone = model.getPatientPhone();
                                    String address = model.getPatientAddress();
                                    String date = model.getAppDate();
                                    String time = model.getAppTime();
                                    String patientID = model.getPatientID();
                                    String appointmentIDRej = model.getAppointmentID();
                                    HashMap<String, Object> patientMap = new HashMap<String, Object>();
                                    patientMap.put("PatientName", name);
                                    patientMap.put("PatientPhone", phone);
                                    patientMap.put("PatientID", patientID);
                                    patientMap.put("PatientAddress", address);
                                    patientMap.put("AppDate", date);
                                    patientMap.put("AppTime", time);
                                    patientMap.put("AppointmentID", appointmentIDRej);

                                    appointRejRef.child("Patients").child(patientID).child("AppointmentRejected").child(appointmentIDRej).updateChildren(patientMap);
                                    appointRejRef.child("Patients").child(patientID).child("AppointmentRequest").child(appointmentIDRej).removeValue();
                                    appointRejRef.child("AppointmentRequest").child(appointmentIDRej).removeValue();
                                    appointConRef.child("AppointmentRejected").child(appointmentIDRej).updateChildren(patientMap);
                                    Toast.makeText(getApplicationContext(), "Appointment Rejected", Toast.LENGTH_SHORT).show();
                                }
                            });
                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewAppointmentReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_appointment_req, parent, false);
                            return new viewAppointmentReqViewHolder(view);
                        }
                    };
                    viewAppointmentRequest.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewAppointmentRequest.setVisibility(View.GONE);
                    noAppointmentReqTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class viewAppointmentReqViewHolder extends RecyclerView.ViewHolder {
        Button acceptBtn, rejectBtn;
        public viewAppointmentReqViewHolder(@NonNull View itemView) {
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