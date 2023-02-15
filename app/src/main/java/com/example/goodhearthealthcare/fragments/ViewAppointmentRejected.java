package com.example.goodhearthealthcare.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.AppointmentRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewAppointmentRejected extends Fragment {

    String userID;
    RecyclerView viewAppointmentRejected;
    String doctorID;
    DatabaseReference patientRef, appointRejRef,appointConRef;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    TextView noAppointmentRejectTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_appointment_rejected, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        loadingBar = new ProgressDialog(getActivity());

        noAppointmentRejectTxt = view.findViewById(R.id.noAppointmentRejectTxt);

        viewAppointmentRejected = view.findViewById(R.id.viewAppointmentRejected);

        viewAppointmentRejected.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewAppointmentRejected.setLayoutManager(linearLayoutManager);

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
        patientRef.child(userID).child("AppointmentRejected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Patients").child(userID).child("AppointmentRejected").limitToLast(50);
                    FirebaseRecyclerOptions<AppointmentRequest> options = new FirebaseRecyclerOptions.Builder<AppointmentRequest>().setQuery(query, AppointmentRequest.class).build();
                    FirebaseRecyclerAdapter<AppointmentRequest, viewAppointmentRejViewHolder> adapter = new FirebaseRecyclerAdapter<AppointmentRequest, viewAppointmentRejViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewAppointmentRejViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final AppointmentRequest model) {
                            //final String PostKey = getRef(position).getKey();
                            holder.setName(model.getPatientName());
                            holder.setPhone(model.getPatientPhone());
                            holder.setAddress(model.getPatientAddress());
                            holder.setDate(model.getAppDate());
                            holder.setTime(model.getAppTime());

                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewAppointmentRejViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_appointment_req_patient, parent, false);
                            return new viewAppointmentRejViewHolder(view);
                        }
                    };
                    viewAppointmentRejected.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewAppointmentRejected.setVisibility(View.GONE);
                    noAppointmentRejectTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class viewAppointmentRejViewHolder extends RecyclerView.ViewHolder {
        public viewAppointmentRejViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView = itemView;
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