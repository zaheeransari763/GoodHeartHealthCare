package com.example.goodhearthealthcare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.goodhearthealthcare.R;

public class CalculateBmi extends Fragment {

    EditText heightInCms, weightInKgs;
    TextView bmiResultHere,analyzeBmi,bmiResultText;
    Button calculateBmi;
    String calculateResult,bmiResultTextStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculate_bmi, container, false);

        heightInCms = view.findViewById(R.id.heightInCms);
        weightInKgs = view.findViewById(R.id.weightInKgs);
        bmiResultHere = view.findViewById(R.id.bmiResultHere);
        bmiResultText = view.findViewById(R.id.bmiResultText);
        analyzeBmi = view.findViewById(R.id.analyzeBmi);
        calculateBmi = view.findViewById(R.id.calculateBmi);
        calculateBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height = heightInCms.getText().toString();
                String weight = weightInKgs.getText().toString();

                if (height.isEmpty() && weight.isEmpty()){
                    Toast.makeText(getContext(), "Field's are empty", Toast.LENGTH_SHORT).show();
                } else {
                    float fWeight = Float.parseFloat(weight);
                    float fHeight = Float.parseFloat(height) / 100;
                    float bmi = fWeight / (fHeight * fHeight);
                    calculateResult = "BMI : " + " " + bmi;
                    analyzeBmi.setVisibility(View.VISIBLE);
                    bmiResultHere.setText(calculateResult);

                    //TO HIDE HE CALCULATE BUTTON
                    calculateBmi.setVisibility(View.GONE);
                    //WE HAVE 3 VISIBILITY (
                    //VISIBLE = IT IS DEFAULT,
                    //GONE = IT WILL MAKE THE TEXT OR VIEW GET INVISIBLE BUT IT WILL ALSO NOT OCCUPY SOME SPACE,
                    //INVISIBLE = IT WILL ALSO MAKE THE TEXT OR VIEW GET INVISIBLE BUT IT WILL OCCUPY SPACE)

                    //TO DISABLE THE TEXT FIELD WHILE CALCULATE BUTTON IS GONE(INVISIBLE)
                    heightInCms.setFocusable(false);
                    weightInKgs.setFocusable(false);

                    analyzeBmi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(bmi < 16){
                                bmiResultTextStr = "Severely Under Weight";
                            }else if(bmi < 18.5){
                                bmiResultTextStr = "Under Weight";
                            }else if(bmi >= 18.5 && bmi <= 24.9){
                                bmiResultTextStr = "Normal Weight";
                            }else if (bmi >= 25 && bmi <= 29.9){
                                bmiResultTextStr = "Overweight";
                            }else{
                                bmiResultTextStr = "Obese";
                            }
                            bmiResultText.setText("Result: "+bmiResultTextStr);
                            bmiResultText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        return view;
    }
}