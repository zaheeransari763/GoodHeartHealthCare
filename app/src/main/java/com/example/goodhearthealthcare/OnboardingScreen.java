package com.example.goodhearthealthcare;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.card.MaterialCardView;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;
import java.util.ArrayList;
public class OnboardingScreen extends AppCompatActivity {
    private FragmentManager fragmentManager;
    MaterialCardView skipOBSBtn;
    LinearLayout onboardingFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(),SplashScreen.class );
            startActivity(mainActivity);
            finish();
        }
        skipOBSBtn = findViewById(R.id.skipOBSBtn);
        onboardingFrameLayout = findViewById(R.id.onboardingFrameLayout);
        fragmentManager = getSupportFragmentManager();
        final PaperOnboardingFragment paperOnboardingFragment = PaperOnboardingFragment.newInstance(getDataforOnboarding());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.onboardingFrameLayout, paperOnboardingFragment);
        fragmentTransaction.commit();
        //  New Line
        skipOBSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(),SplashScreen.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();
            }
        });
    }
    private ArrayList<PaperOnboardingPage> getDataforOnboarding() {
        PaperOnboardingPage nursingcarepage = new PaperOnboardingPage("24 Hours Nursing Care", "Book a Home Visit Now! Get Nursing Care services to help patients recover quickly at home.", Color.parseColor("#ddff9d"), R.drawable.nursing_p, R.drawable.black_circle);
        PaperOnboardingPage dentistrypage = new PaperOnboardingPage("Dentistry Services", "Looking to find a Dentist who can provide Dental Care Regularly? You are at the right place. Contact us today!", Color.parseColor("#22eaaa"), R.drawable.dentist_p, R.drawable.black_circle);
        PaperOnboardingPage diabetescarepage = new PaperOnboardingPage("Home Diabetes Care", "Are you looking for a Skilled and Professional nurse for a Diabetic patient? We are here to provide proper care in the comfort of their Home. ", Color.parseColor("#35b6ed"), R.drawable.diabetes_p, R.drawable.black_circle);
        PaperOnboardingPage labtestpage = new PaperOnboardingPage("Lab Test At Home", "Book Lab Test and General Health checkup at the tip of your finger and take the steps towards a healthy life. ", Color.parseColor("#ff8273"), R.drawable.labtest_p, R.drawable.black_circle);
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(nursingcarepage);
        elements.add(dentistrypage);
        elements.add(diabetescarepage);
        elements.add(labtestpage);
        String ind = elements.get(3).getTitleText();
        if (ind.equals("Lab Test At Home")) {
            onboardingFrameLayout.setVisibility(View.VISIBLE);
        } else {
            onboardingFrameLayout.setVisibility(View.GONE);
        }
        return elements;
    }
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();
    }
}