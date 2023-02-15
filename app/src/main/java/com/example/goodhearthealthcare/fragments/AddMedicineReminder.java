package com.example.goodhearthealthcare.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.goodhearthealthcare.MainActivity;
import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.AddMedicine;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddMedicineReminder extends Fragment {

    RelativeLayout addMedicineBtn, submitMedicineBtn;
    LinearLayout layout_list;
    MaterialCardView submitMedicineListCard;
    ArrayList<AddMedicine> medList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_medicine_reminder, container, false);

        submitMedicineListCard = view.findViewById(R.id.submitMedicineListCard);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Medicine Reminder");

        layout_list = view.findViewById(R.id.layout_list);
        submitMedicineBtn = view.findViewById(R.id.submitMedicineBtn);
        submitMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfValidAndRead()){
                }
            }
        });

        addMedicineBtn = view.findViewById(R.id.addMedicineBtn);
        addMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView();
            }
        });
        return view;
    }

    private boolean checkIfValidAndRead() {
        medList.clear();
        boolean result = true;
        for (int i = 0; i<layout_list.getChildCount(); i++){
            View medicineView = layout_list.getChildAt(i);
            TextInputLayout mName = medicineView.findViewById(R.id.medicineNameLay);
            TextInputLayout mTime = medicineView.findViewById(R.id.medicineTimeLay);
            String mNameStr = mName.getEditText().getText().toString();
            String mTimeStr = mTime.getEditText().getText().toString();
            AddMedicine addMedicine = new AddMedicine();

            if (!mNameStr.isEmpty() || !mTimeStr.isEmpty()){
                addMedicine.setMedName(mNameStr);
                addMedicine.setMedTime(mTimeStr);
            } else {
                result = false;
                break;
            }
            medList.add(addMedicine);
        }
        return result;
    }

    private void addView() {
        View medicineView = getLayoutInflater().inflate(R.layout.medicine_add_row,null,false);

        TextInputLayout mName = medicineView.findViewById(R.id.medicineNameLay);
        TextInputLayout mTime = medicineView.findViewById(R.id.medicineTimeLay);
        ImageView mLayClose = medicineView.findViewById(R.id.clearMedicineLay);
        mLayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMediView(medicineView);
            }
        });

        layout_list.addView(medicineView);
    }

    private void removeMediView(View v) {
        layout_list.removeView(v);
    }
}