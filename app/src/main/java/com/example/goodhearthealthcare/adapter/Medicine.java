package com.example.goodhearthealthcare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodhearthealthcare.R;
import com.example.goodhearthealthcare.modal.AddMedicine;

import java.util.ArrayList;

public class Medicine extends RecyclerView.Adapter<Medicine.MedicineView> {

    ArrayList<AddMedicine> addMedicineList = new ArrayList<>();

    public Medicine(ArrayList<AddMedicine> addMedicineList) {
        this.addMedicineList = addMedicineList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_view_row, parent ,false);
        return new MedicineView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineView holder, int position) {
        AddMedicine addMedicine = addMedicineList.get(position);
        holder.medicineName.setText(addMedicine.getMedName());
        holder.medicineTimer.setText(addMedicine.getMedTime());
    }

    @Override
    public int getItemCount() {
        return addMedicineList.size();
    }

    public class MedicineView extends RecyclerView.ViewHolder{
        TextView medicineName, medicineTimer;

        public MedicineView(@NonNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.medicineName);
            medicineTimer = itemView.findViewById(R.id.medicineTimer);
        }
    }
}
