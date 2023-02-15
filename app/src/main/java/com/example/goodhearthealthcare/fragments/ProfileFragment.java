package com.example.goodhearthealthcare.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goodhearthealthcare.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    String currUserId;
    DatabaseReference profileRef;
    TextView userPhone, userAltPhone, userEmail, userAddress, userDOB, userMaritalStatus, userAge, userGender, userId;
    ProgressDialog loadingBar;

    //VARIABLES FOR PROFILE IMAGE UPLOAD
    CircleImageView setupProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        loadingBar = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        currUserId = mAuth.getCurrentUser().getUid();
        profileRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currUserId);

        setupProfileImage = view.findViewById(R.id.setupProfileImage);
        userPhone = view.findViewById(R.id.userPhone);
        userAltPhone = view.findViewById(R.id.userAltPhone);
        userEmail = view.findViewById(R.id.userEmail);
        userAddress = view.findViewById(R.id.userAddress);
        userDOB = view.findViewById(R.id.userDOB);
        userMaritalStatus = view.findViewById(R.id.userMaritalStatus);
        userAge = view.findViewById(R.id.userAge);
        userGender = view.findViewById(R.id.userGender);
        userId = view.findViewById(R.id.userId);

        loadingBar.setTitle("please wait...");
        loadingBar.setMessage("fetching your details have patience");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.child("Phone").getValue().toString();
                String altPhone = dataSnapshot.child("AltPhone").getValue().toString();
                String email = dataSnapshot.child("Email").getValue().toString();
                String add = dataSnapshot.child("Address").getValue().toString();
                String dob = dataSnapshot.child("DOB").getValue().toString();
                String mStatus = dataSnapshot.child("MaritalStatus").getValue().toString();
                String age = dataSnapshot.child("Age").getValue().toString();
                String gender = dataSnapshot.child("Gender").getValue().toString();
                String fname = dataSnapshot.child("fName").getValue().toString();
                String lname = dataSnapshot.child("lName").getValue().toString();
                String fnameStr = fname.substring(0, 3).toUpperCase();
                String lnameStr = lname.substring(0, 3).toUpperCase();
                String phoneStr = phone.substring(0, 4).toUpperCase();
                String uniqueId = fnameStr + lnameStr + phoneStr;

                userPhone.setText(phone);
                userAltPhone.setText(altPhone);
                userEmail.setText(email);
                userAddress.setText(add);
                userDOB.setText(dob);
                userMaritalStatus.setText(mStatus);
                userAge.setText(age);
                userGender.setText(gender);
                userId.setText("ID - " + uniqueId);
                loadingBar.dismiss();

                final String image = dataSnapshot.child("image").getValue().toString();
                if (!image.equals("default"))
                {
                    Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into(setupProfileImage);
                    Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(setupProfileImage, new Callback() {
                        @Override
                        public void onSuccess()
                        { }

                        @Override
                        public void onError()
                        {
                            Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into(setupProfileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}