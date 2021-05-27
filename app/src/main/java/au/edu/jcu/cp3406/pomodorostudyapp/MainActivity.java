package au.edu.jcu.cp3406.pomodorostudyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView countdown_text_view;
    private Button countdown_button;
    private Button set_time_button;
    private Button reset_button;
    private CountDownTimer studyTimer; // Imports built in Android Class Countdown Timer.
    private CountDownTimer restTimer;
    private EditText edit_text_user_input;


    private long studyTimeLeftInMilliseconds; // Stores milliseconds of user inputted time.
    private long timeLeftInMilliseconds;
    private long restTimeLeftInMilliseconds = 300000;
    private boolean isRunning;

    private final long countDownInterval = 1000; // Interval used to demonstrate the rate of each second.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(MainActivity.this, "Please enter the amount of time you would like to study for then click Start!", Toast.LENGTH_LONG).show();
        countdown_text_view = findViewById(R.id.countdown_text_view); // The countdown timer display.
        countdown_button = findViewById(R.id.countdown_button);// Start / Stop button for Study time.
        set_time_button = findViewById(R.id.set_time_button); // Button for setting time in minutes.
        reset_button = findViewById(R.id.reset_button); // Resets the time to the original inputted time.
        edit_text_user_input = findViewById(R.id.edit_text_user_input); // The text box for users to input a time.

        reset_button.setOnClickListener(view -> studyTimerReset());
        timerTextUpdate();

        set_time_button.setOnClickListener(view -> {
            String input = edit_text_user_input.getText().toString();
            // When "Set Time" is pressed, these conditions will be checked through the users inputted time.
            if (input.length() == 0) {
                Toast.makeText(MainActivity.this, "Please enter a number.", Toast.LENGTH_SHORT).show();
                return;
            }
            long millisecondsInput = Long.parseLong(input) * 60000;
            if (millisecondsInput == 0) {
                Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (millisecondsInput >= 3600000) {
                Toast.makeText(MainActivity.this, "Please enter a time below 60 minutes", Toast.LENGTH_SHORT).show();
                return;
            }
            setStudyTime(millisecondsInput);
            edit_text_user_input.setText("");
            countdown_button.setVisibility(View.VISIBLE);
        });
        countdown_button.setOnClickListener(view -> studyTimerRunning());
        timerTextUpdate();

    }

    // Resets remaining time left to original user inputted study time.
    public void studyTimerReset() {
        studyTimeLeftInMilliseconds = timeLeftInMilliseconds;
        timerTextUpdate();
    }

    // Sets study time.
    private void setStudyTime(long set_milliseconds) {
        timeLeftInMilliseconds = set_milliseconds;
        studyTimerReset();
        closeKeyboard();
    }

    // Start functionality of the timer. The user input is stored in studyTimeLeftInMilliseconds.
    public void timerStart() {
        studyTimer = new CountDownTimer(studyTimeLeftInMilliseconds, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                studyTimeLeftInMilliseconds = millisUntilFinished;
                timerTextUpdate();
            }

            //onFinish is automatically called regardless due to the nature of the CountDownTimer Class.
            // Changes to use Rest period timer.
            @Override
            public void onFinish() {
                studyTimer.cancel();
                Toast.makeText(MainActivity.this, "Starting timer for break. Enjoy it wisely...", Toast.LENGTH_LONG).show();
                reset_button.setVisibility(View.INVISIBLE);
                restTimer = new CountDownTimer(restTimeLeftInMilliseconds, countDownInterval) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        studyTimeLeftInMilliseconds = restTimeLeftInMilliseconds;
                        restTimeLeftInMilliseconds = millisUntilFinished;
                        timerTextUpdate();
                    }

                    @Override
                    public void onFinish() {
                        restTimer.cancel();
                        studyTimerStop();
                        updateButton();
                        reset_button.setVisibility(View.VISIBLE);
                    }
                };
                restTimer.start(); // Starts the Rest Timer once Study timer ends and calls onFinish.
                isRunning = true;
                countdown_button.setText(R.string.pause_break);
                updateButton();
            }
        };
        isRunning = true;
        studyTimer.start(); // Starts the Study Timer.
        countdown_button.setText(R.string.pause_pomodoro);
        updateButton();
    }

    //If isRunning is false, stop the timer. Else isRunning is True, start the timer.
    public void studyTimerRunning() {
        if (isRunning) { // If False.
            studyTimerStop();
        } else { // If True.
            timerStart();
            reset_button.setVisibility(View.INVISIBLE);
        }
    }

    // Cancels study timer until user decides to press "Start" again or go to settings.
    public void studyTimerStop() {
        studyTimer.cancel();
        countdown_button.setText(R.string.start_study);
        reset_button.setVisibility(View.VISIBLE);
        isRunning = false;
    }


    // Converts the user's set time into minutes and seconds then display them as string in the TextView.
    public void timerTextUpdate() {
        int minutes = (int) studyTimeLeftInMilliseconds / 60000;
        int seconds = (int) studyTimeLeftInMilliseconds % 60000 / 1000;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdown_text_view.setText(timeLeft);
    }

    // Displays or hide buttons depending on boolean isRunning.
    private void updateButton() {
        if (isRunning) {
            edit_text_user_input.setVisibility(View.INVISIBLE);
            set_time_button.setVisibility(View.INVISIBLE);
        } else {
            edit_text_user_input.setVisibility(View.VISIBLE);
            set_time_button.setVisibility(View.VISIBLE);
        }
    }


    //"onSaveInstanceState" will save the time since the activity is destroyed when rotated.
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("time", studyTimeLeftInMilliseconds);
    }

    // "onRestoreInstanceState" will restore that time when the
    // screen is rotated instead of resetting the time back to what is stored in "studyTimeLeftInMilliseconds".
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        studyTimeLeftInMilliseconds = savedInstanceState.getLong("time");
        countdown_text_view.setText(String.valueOf(studyTimeLeftInMilliseconds));
        timerTextUpdate();
    }

    // Closes keyboard after pressing the "Set Time" button.
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Clicking the settings button will take the user to the Settings.
    public void settingsClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}