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
import android.widget.Button;
import android.widget.TextView;

import com.example.goodhearthealthcare.MainActivity;
import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.Staff;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewNurseList extends Fragment {

    RecyclerView viewNurseListForAppointment;
    DatabaseReference nurseList;
    ProgressDialog loadingBar;
    String patientIDStr, currUserId;
    FirebaseAuth mAuth;
    TextView noNurseTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_nurse_list, container, false);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(getActivity());

        noNurseTxt = view.findViewById(R.id.noNurseTxt);
        viewNurseListForAppointment = view.findViewById(R.id.viewNurseListForAppointment);
        viewNurseListForAppointment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewNurseListForAppointment.setLayoutManager(linearLayoutManager);
        nurseList = FirebaseDatabase.getInstance().getReference();

        currUserId = mAuth.getCurrentUser().getUid();

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        ((MainActivity) getActivity()).setDrawer_Locked();

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
        nurseList.child("Staffs").child("Nurse").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Query query = nurseList.child("Staffs").child("Nurse").limitToLast(50);
                    FirebaseRecyclerOptions<Staff> options = new FirebaseRecyclerOptions.Builder<Staff>().setQuery(query, Staff.class).build();
                    FirebaseRecyclerAdapter<Staff, viewNurseListViewHolder> adapter = new FirebaseRecyclerAdapter<Staff, viewNurseListViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewNurseListViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Staff model) {
                            holder.setName(model.getStaffName());
                            holder.setHosName(model.getHospitalName());
                            holder.setHospAddress(model.getHospitalAddress());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String sPhone = model.getStaffPhone();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("staffPhone", sPhone);
                                    NurseProfileFragment profileView = new NurseProfileFragment();
                                    profileView.setArguments(bundle);
                                    ((MainActivity) getActivity()).replaceFragment(profileView,"fragmentC");
                                }
                            });
                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewNurseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_nurse_view, parent, false);
                            return new viewNurseListViewHolder(view);
                        }
                    };
                    viewNurseListForAppointment.setAdapter(adapter);
                    adapter.startListening();
                } else {
                    viewNurseListForAppointment.setVisibility(View.GONE);
                    noNurseTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class viewNurseListViewHolder extends RecyclerView.ViewHolder {
        public viewNurseListViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setName(String fname) {
            TextView firstname = itemView.findViewById(R.id.nurseName);
            firstname.setText(fname);
        }

        public void setHosName(String hospName) {
            TextView stdID = itemView.findViewById(R.id.doctorHospitalName);
            stdID.setText(hospName);
        }

        public void setHospAddress(String hospAddress) {
            TextView stdID = itemView.findViewById(R.id.doctorHospitalAddress);
            stdID.setText(hospAddress);
        }
    }
}