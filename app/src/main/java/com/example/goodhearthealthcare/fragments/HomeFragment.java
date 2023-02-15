package com.example.goodhearthealthcare.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.goodhearthealthcare.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String CHANNEL_ID = "My channel";
    private static final int NOTIFICATION_ID = 100;

    TextInputLayout mEditTextInput;
    TextView mTextViewCountDown;
    Button mButtonStartPause, mButtonReset, mButtonSet;
    LinearLayout diabeticMedicineReminder;
    CountDownTimer mCountDownTimer;
    boolean mTimerRunning;
    long mTimeLeftInMillis;
    long mStartTimeInMillis;
    long mEndTime;

    ImageView calculateBmiImg, viewDoctorImg, viewNurseImg, viewLabsImg, viewAppliedLabImg, viewConfirmedLabImg, viewRejectedLabImg;
    //VARIABLES FOR APPOINTMENT
    ImageView viewAppliedAptImg, viewConfirmedAptImg, viewRejectedAptImg;

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String currUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        currUserId = mAuth.getCurrentUser().getUid();

        diabeticMedicineReminder = view.findViewById(R.id.diabeticMedicineReminder);

        userRef.child(currUserId).child("OldMedi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String diabetesCheck = snapshot.child("HaveDiabetes").getValue().toString();
                    if (diabetesCheck.equals("YES")){
                        diabeticMedicineReminder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //TIMER LOGIC
        mEditTextInput = view.findViewById(R.id.edit_text_input);
        mTextViewCountDown = view.findViewById(R.id.textViewCountdown);
        mButtonStartPause = view.findViewById(R.id.buttonStartPause);
        mButtonReset = view.findViewById(R.id.buttonReset);
        mButtonSet = view.findViewById(R.id.button_set);
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mEditTextInput.getEditText().getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(getContext(), "Field cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(getContext(), "Number is negative", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                mEditTextInput.getEditText().setText("");
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });

        //updateCountDownText();

        viewNurseImg = view.findViewById(R.id.viewNurseImg);
        viewNurseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewNurseList viewNurseList = new ViewNurseList();
                replaceFragment(viewNurseList, "fragmentB");
            }
        });

        viewAppliedLabImg = view.findViewById(R.id.viewAppliedLabImg);
        viewAppliedLabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppliedLabTest appliedLabTest = new AppliedLabTest();
                replaceFragment(appliedLabTest, "fragmentB");
            }
        });

        viewConfirmedLabImg = view.findViewById(R.id.viewConfirmedLabImg);
        viewConfirmedLabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmedLabTest appliedLabTest = new ConfirmedLabTest();
                replaceFragment(appliedLabTest, "fragmentB");
            }
        });

        viewRejectedLabImg = view.findViewById(R.id.viewRejectedLabImg);
        viewRejectedLabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RejectedLabTest appliedLabTest = new RejectedLabTest();
                replaceFragment(appliedLabTest, "fragmentB");
            }
        });

        viewAppliedAptImg = view.findViewById(R.id.viewAppliedAptImg);
        viewAppliedAptImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAppliedAppointment viewAppliedAppointment = new ViewAppliedAppointment();
                replaceFragment(viewAppliedAppointment, "fragmentB");
            }
        });

        viewConfirmedAptImg = view.findViewById(R.id.viewConfirmedAptImg);
        viewConfirmedAptImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewConfirmedAppointment confirmedAppointment = new ViewConfirmedAppointment();
                replaceFragment(confirmedAppointment, "fragmentB");
            }
        });

        viewRejectedAptImg = view.findViewById(R.id.viewRejectedAptImg);
        viewRejectedAptImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAppointmentRejected rejected = new ViewAppointmentRejected();
                replaceFragment(rejected, "fragmentB");
            }
        });

        viewDoctorImg = view.findViewById(R.id.viewDoctorImg);
        viewDoctorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDoctorsList viewDoctorsList = new ViewDoctorsList();
                replaceFragment(viewDoctorsList, "fragmentB");
            }
        });

        viewLabsImg = view.findViewById(R.id.viewLabsImg);
        viewLabsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewLabsList viewLabsList = new ViewLabsList();
                replaceFragment(viewLabsList, "fragmentB");
            }
        });

        calculateBmiImg = view.findViewById(R.id.calculateBmiImg);
        calculateBmiImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalculateBmi calculateBmi = new CalculateBmi();
                //getParentFragmentManager().beginTransaction().replace(R.id.container, factsPage).commit();
                replaceFragment(calculateBmi, "fragmentB");
            }
        });

        return view;
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
                generateNotification();
            }
        }.start();
        mTimerRunning = true;
        updateWatchInterface();
    }

    private void generateNotification() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("It's time for your medicine")
                    .setSubText("Hey Buddy!")
                    .setChannelId(CHANNEL_ID)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH));
        } else {
            notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("Hey Buddy!")
                    .setSubText("It's time for your medicine")
                    .build();
        }

        nm.notify(NOTIFICATION_ID, notification);
    }

    private void closeKeyboard() {
        View viewKeyBoard = getActivity().getCurrentFocus();
        if (viewKeyBoard != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewKeyBoard.getWindowToken(), 0);
        }
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }

    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;

        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.setVisibility(View.GONE);
            mButtonSet.setVisibility(View.GONE);
            mButtonReset.setVisibility(View.GONE);
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.GONE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        //Get current fragment placed in container
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.container);
        //Prevent adding same fragment on top
        if (currentFragment.getClass() == fragment.getClass()) {
            return;
        }
        //If fragment is already on stack, we can pop back stack to prevent stack infinite growth
        if (getParentFragmentManager().findFragmentByTag(tag) != null) {
            getParentFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //Otherwise, just replace fragment
        getParentFragmentManager().beginTransaction().addToBackStack(tag).replace(R.id.container, fragment, tag).commit();
    }
}