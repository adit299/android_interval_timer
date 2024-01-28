package com.example.intervaltimer.timer;

public enum TimerButtonAction {
    START("Start"),
    PAUSE("Pause"),
    RESUME("Resume"),
    RESET("Reset"),
    BACK("Back");

    public final String text;

   TimerButtonAction(String text) {
        this.text = text;
   }
}
