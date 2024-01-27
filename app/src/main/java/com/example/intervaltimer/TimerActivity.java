package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import com.example.intervaltimer.timer.IntervalCountDownTimer;

public class TimerActivity extends AppCompatActivity {
    private String CHANNEL_ID = "1";
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManager;
    private int beepCounter;
    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView numberOfBeepsVal;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    private IntervalCountDownTimer timer;
    private long durationMillisInput;
    private long intervalMillisInput;
    private int numOfBeepsInput;
    private Boolean startButtonState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        beepCounter = 0;

        // Extract initial timer values from saved instance state
        Intent intent = getIntent();
        durationMillisInput = intent.getLongExtra(MainActivity.DURATION_INTENT_KEY, 0);
        intervalMillisInput = intent.getLongExtra(MainActivity.INTERVAL_INTENT_KEY, 0);
        numOfBeepsInput = intent.getIntExtra(MainActivity.BEEPS_INTENT_KEY, 0);

        // Initialize timer
        timer = new IntervalCountDownTimer(
                createDurationTimer(durationMillisInput, 100),
                createIntervalTimer(durationMillisInput, intervalMillisInput)
        );

        // Request permissions for notifications
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TimerActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSION_PHONE_STATE);
        }

        // Initialize notification channel and notification settings
        createNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("A beep was triggered!!")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager = NotificationManagerCompat.from(this);

        // Get button elements
        backButton = findViewById(R.id.back_button);
        startButton = findViewById(R.id.start_timer_button);
        resetButton = findViewById(R.id.reset_timer_button);

        // Set button click behaviour
        backButton.setOnClickListener(view -> {
            timer.cancelTimers();
            TimerActivity.this.finish();
        });
        startButton.setOnClickListener(view -> {
            timer.startTimers();
            startButton.setEnabled(false);
            startButtonState = false;
        });
        resetButton.setOnClickListener(view -> {
            timer.cancelTimers();
            resetTimerValues();
        });

        // Get text views
        durationVal = findViewById(R.id.duration_val);
        intervalTimingVal = findViewById(R.id.interval_timing_val);
        numberOfBeepsVal = findViewById(R.id.number_of_beeps_val);

        // Initialize text values
        durationVal.setText(formatTimeString(durationMillisInput));
        intervalTimingVal.setText(formatTimeString(intervalMillisInput));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Customize the notification to vibrate and play a noise (can also flash lights)
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
        );
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Initialize the CountDownTimer for the duration timer field.
     *
     * @param durationMillis Duration value in milliseconds
     * @param intervalMillis Interval value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createDurationTimer(long durationMillis, long intervalMillis) {
        CountDownTimer durationTimer = new CountDownTimer(durationMillis, intervalMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                durationVal.setText(formatTimeString(millisUntilFinished));
                intervalTimingVal.setText(formatTimeString(millisUntilFinished % (intervalMillisInput)));
            }

            @Override
            public void onFinish() {
                durationVal.setText(formatTimeString(0));
            }
        };
        return durationTimer;
    }

    /**
     * Initialize the CountDownTimer for the interval field.
     *
     * @param durationMillis Duration value in milliseconds
     * @param intervalMillis Interval value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createIntervalTimer(long durationMillis, long intervalMillis) {
        final Boolean[] isFirst = {true};
        CountDownTimer intervalTimer = new CountDownTimer(durationMillis, intervalMillis) {
            @SuppressLint("MissingPermission")
            @Override
            public void onTick(long millisUntilFinished) {
                numberOfBeepsVal.setText(formatNumberOfBeeps(beepCounter));
                beepCounter += 1;
                if(isFirst[0]) {
                    isFirst[0] = false;
                    return;
                }
                notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilder.build());
            }
            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilder.build());
                intervalTimingVal.setText(formatTimeString(0));
                numberOfBeepsVal.setText(formatNumberOfBeeps(beepCounter));
            }
        };
        return intervalTimer;
    }

    /**
     * Resets the timer
     */
    private void resetTimerValues() {
        // Reset text to the initial values
        durationVal.setText(formatTimeString(durationMillisInput));
        intervalTimingVal.setText(formatTimeString(intervalMillisInput));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
        startButton.setEnabled(true);
        startButtonState = true;
        beepCounter = 0;

    }

    /**
     * Helper to format time from milliseconds into String.
     *
     * @param millis The time value in milliseconds.
     * @return The formatted time.
     */
    private String formatTimeString(long millis) {
        int tenths = (int) (millis / 100) % 10;
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours = (int) ((millis / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, tenths);
    }

    /**
     * Helper to format the number of beeps text
     *
     * @param numberOfBeeps The number of beeps to display.
     * @return The formatted number of beeps.
     */
    private String formatNumberOfBeeps(int numberOfBeeps) {
        return String.format("%d / %d", numberOfBeeps, numOfBeepsInput);
    }
}