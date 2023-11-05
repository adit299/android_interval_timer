package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.intervaltimer.validation.InputValidator;

public class MainActivity extends AppCompatActivity {

    Spinner durationHours;
    Spinner durationMinutes;
    Spinner durationSeconds;
    Spinner intervalTimerHours;
    Spinner intervalTimerMinutes;
    Spinner intervalTimerSeconds;
    Button submitBtn;
    Button resetBtn;

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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputValidator inputValidator = new InputValidator(
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
                }
                else {
                    // Throw an error
                }

            }
        });

    }





}