package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.intervaltimer.validation.InputValidator;

public class MainActivity extends AppCompatActivity {

    // Constants
    public static final String DURATION_INTENT_KEY = "durationMillis";
    public static final String INTERVAL_INTENT_KEY = "intervalMillis";
    public static final String BEEPS_INTENT_KEY = "numOfBeeps";
    // UI Elements
    Spinner durationHours;
    Spinner durationMinutes;
    Spinner durationSeconds;
    Spinner intervalTimerHours;
    Spinner intervalTimerMinutes;
    Spinner intervalTimerSeconds;
    Button submitBtn;
    Button resetBtn;
    // Dependencies
    InputValidator inputValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        durationHours = findViewById(R.id.duration_time_hours);
        durationMinutes = findViewById(R.id.duration_time_minutes);
        durationSeconds = findViewById(R.id.duration_time_seconds);
        intervalTimerHours = findViewById(R.id.interval_time_hours);
        intervalTimerMinutes = findViewById(R.id.interval_time_minutes);
        intervalTimerSeconds = findViewById(R.id.interval_time_seconds);
        submitBtn = findViewById(R.id.submitBtn);
        resetBtn = findViewById(R.id.resetBtn);

        ArrayAdapter<CharSequence> hoursAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.hours_array,
            android.R.layout.simple_spinner_item
        );

        ArrayAdapter<CharSequence> minutesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.minutes_array,
            android.R.layout.simple_spinner_item
        );

        ArrayAdapter<CharSequence> secondsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.seconds_array,
            android.R.layout.simple_spinner_item
        );

        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        durationHours.setAdapter(hoursAdapter);
        intervalTimerHours.setAdapter(hoursAdapter);

        durationMinutes.setAdapter(minutesAdapter);
        intervalTimerMinutes.setAdapter(minutesAdapter);

        durationSeconds.setAdapter(secondsAdapter);
        intervalTimerSeconds.setAdapter(secondsAdapter);

        submitBtn.setOnClickListener(v -> {
            inputValidator = new InputValidator(
                durationHours.getSelectedItem().toString(),
                durationMinutes.getSelectedItem().toString(),
                durationSeconds.getSelectedItem().toString(),
                intervalTimerHours.getSelectedItem().toString(),
                intervalTimerMinutes.getSelectedItem().toString(),
                intervalTimerSeconds.getSelectedItem().toString()
            );

            if(inputValidator.fullValidation()) {
                // Proceed to next screen, passing the values
                // Also set the number of beeps
                redirectToSecondActivity();
            }
            else {
                // Show an error
                Toast.makeText(getApplicationContext(), "ERROR: Invalid Input. Try Again.", Toast.LENGTH_LONG).show();
            }
        });

        resetBtn.setOnClickListener(v -> {
            resetInputs();
        });
    }

    private void redirectToSecondActivity() {
        Intent intent = new Intent(MainActivity.this, TimerActivity.class);
        intent.putExtra(DURATION_INTENT_KEY, inputValidator.getDurationMilliseconds());
        intent.putExtra(INTERVAL_INTENT_KEY, inputValidator.getIntervalMilliseconds());
        intent.putExtra(BEEPS_INTENT_KEY, inputValidator.getNumOfBeeps());
        startActivity(intent);
    }

    private void resetInputs() {
        durationHours.setSelection(0);
        durationMinutes.setSelection(0);
        durationSeconds.setSelection(0);
        intervalTimerHours.setSelection(0);
        intervalTimerMinutes.setSelection(0);
        intervalTimerSeconds.setSelection(0);
    }
}