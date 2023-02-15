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

import com.example.goodhearthealthcare.MainActivity;
import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.Labs;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewLabsList extends Fragment {

    RecyclerView viewLabsList;
    DatabaseReference hospitalRef;
    ProgressDialog loadingBar;
    TextView noLabListTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_labs_list, container, false);

        loadingBar = new ProgressDialog(getActivity());

        viewLabsList = view.findViewById(R.id.viewLabsList);
        noLabListTxt = view.findViewById(R.id.noLabListTxt);
        viewLabsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewLabsList.setLayoutManager(linearLayoutManager);

        hospitalRef = FirebaseDatabase.getInstance().getReference().child("Labs");
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
        hospitalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("Labs").limitToLast(50);
                    FirebaseRecyclerOptions<Labs> options = new FirebaseRecyclerOptions.Builder<Labs>().setQuery(query, Labs.class).build();
                    FirebaseRecyclerAdapter<Labs, viewLabsViewHolder> adapter = new FirebaseRecyclerAdapter<Labs, viewLabsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull viewLabsViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Labs model) {
                            holder.setName(model.getLabName());
                            holder.setPhone(model.getLabPhone());
                            holder.setHospitalID(model.getLabID());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String labID = model.getLabID();
                                    String labName = model.getLabName();
                                    String labPhone = model.getLabPhone();
                                    String labEmail = model.getLabEmail();
                                    String labAdd = model.getLabAddress();
                                    String labTo = model.getToTime();
                                    String labFrom = model.getFromTime();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("labID", labID);
                                    bundle.putString("labName", labName);
                                    bundle.putString("labPhone", labPhone);
                                    bundle.putString("labEmail", labEmail);
                                    bundle.putString("labAdd", labAdd);
                                    bundle.putString("labTo", labTo);
                                    bundle.putString("labFrom", labFrom);
                                    ViewLabProfile profileView = new ViewLabProfile();
                                    profileView.setArguments(bundle);
                                    ((MainActivity) getActivity()).replaceFragment(profileView, "fragmentC");
                                }
                            });
                            loadingBar.dismiss();
                        }

                        @NonNull
                        @Override
                        public viewLabsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_labs_view, parent, false);
                            return new viewLabsViewHolder(view);
                        }
                    };
                    viewLabsList.setAdapter(adapter);
                    adapter.startListening();
                }  else {
                    viewLabsList.setVisibility(View.GONE);
                    noLabListTxt.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class viewLabsViewHolder extends RecyclerView.ViewHolder {
        public viewLabsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setName(String name) {
            TextView firstname = (TextView) itemView.findViewById(R.id.labName);
            firstname.setText("Name: " + name);
        }

        public void setPhone(String phone) {
            TextView phoneTv = (TextView) itemView.findViewById(R.id.labPhone);
            phoneTv.setText("Phone: " + phone);
        }

        public void setHospitalID(String labID) {
            TextView labIDTV = (TextView) itemView.findViewById(R.id.labID);
            labIDTV.setText("Lab ID: " + labID);
        }

        /*public void setImagee(Context ctx, String image)
        {
            CircleImageView donorimage = (CircleImageView) mView.findViewById(R.id.donor_profile_image);
            Picasso.with(ctx).load(image).into(donorimage);
        }*/
    }
}