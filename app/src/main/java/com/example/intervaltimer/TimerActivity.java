package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.example.intervaltimer.timer.IntervalCountDownTimer;
import com.example.intervaltimer.timer.TimerButtonAction;
import com.example.intervaltimer.timer.TimerUtils;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class TimerActivity extends AppCompatActivity {
    private String CHANNEL_ID = "1";
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private NotificationCompat.Builder notificationBuilderProgress;
    private NotificationCompat.Builder notificationBuilderAlarm;
    private NotificationManagerCompat notificationManager;
    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView setsVal;
    private CircularProgressIndicator intervalProgressIndicator;
    private CircularProgressIndicator durationProgressIndicator;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    private IntervalCountDownTimer timer;
    private long durationMillisInput;
    private long intervalMillisInput;
    private int setsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Extract initial timer values from saved instance state
        Intent intent = getIntent();
        durationMillisInput = intent.getLongExtra(MainActivity.DURATION_INTENT_KEY, 0);
        intervalMillisInput = intent.getLongExtra(MainActivity.INTERVAL_INTENT_KEY, 0);
        setsInput = intent.getIntExtra(MainActivity.BEEPS_INTENT_KEY, 0);

        // Get text views
        durationVal = findViewById(R.id.duration_val);
        intervalTimingVal = findViewById(R.id.interval_timing_val);
        setsVal = findViewById(R.id.number_of_beeps_val);

        // Get progress indicators
        intervalProgressIndicator = findViewById(R.id.interval_progress_indicator);
        intervalProgressIndicator.setMax(1000);
        durationProgressIndicator = findViewById(R.id.duration_progress_indicator);
        durationProgressIndicator.setMax((int)(durationMillisInput / 100));

        notificationBuilderProgress = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Time remaining before next interval...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOnlyAlertOnce(true);

        notificationBuilderAlarm = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("An interval has finished!!")
                .setTimeoutAfter(2000)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        notificationManager = NotificationManagerCompat.from(this);


        // Initialize timer
        timer = new IntervalCountDownTimer(
                    durationVal,
                    intervalTimingVal,
                    setsVal,
                    intervalProgressIndicator,
                    durationProgressIndicator,
                    durationMillisInput,
                    intervalMillisInput,
                    setsInput,
                    notificationBuilderProgress,
                    notificationBuilderAlarm,
                    notificationManager
                );

        // Request permissions for notifications
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TimerActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSION_PHONE_STATE);
        }


        // Initialize notification channel and notification settings
        createNotificationChannel();

        // Initialize text view values
        durationVal.setText(TimerUtils.formatTimeString(durationMillisInput));
        intervalTimingVal.setText(TimerUtils.formatTimeString(intervalMillisInput));
        setsVal.setText(TimerUtils.formatSets(0, setsInput));


        // Get button elements
        backButton = findViewById(R.id.back_button);
        startButton = findViewById(R.id.start_timer_button);
        resetButton = findViewById(R.id.reset_timer_button);

        // Set button click behaviour
        backButton.setOnClickListener(view -> {
            processButtonPress(TimerButtonAction.BACK);
        });
        startButton.setOnClickListener(view -> {
            if (timer.isRunning()) {
                processButtonPress(TimerButtonAction.PAUSE);
            } else if (timer.getRemainingDuration() > 0
                    && timer.getRemainingDuration() < timer.getTotalDuration()) {
                processButtonPress(TimerButtonAction.RESUME);
            } else if (timer.getRemainingDuration() == timer.getTotalDuration()) {
                processButtonPress(TimerButtonAction.START);
            } else {
                // Timer is done, do nothing
            }
        });
        resetButton.setOnClickListener(view -> {
            processButtonPress(TimerButtonAction.RESET);
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    private void processButtonPress(TimerButtonAction action) {
        switch (action) {
            case START:
                timer.startTimer();
                startButton.setText(TimerButtonAction.PAUSE.text);
                break;
            case PAUSE:
                timer.pauseTimer();
                startButton.setText(TimerButtonAction.RESUME.text);
                break;
            case RESUME:
                timer.resumeTimer();
                startButton.setText(TimerButtonAction.PAUSE.text);
                break;
            case RESET:
                timer.resetTimer();
                startButton.setEnabled(true);
                startButton.setText(TimerButtonAction.START.text);
                break;
            case BACK:
                timer.cancelTimer();
                TimerActivity.this.finish();
                break;
        }
    }
}