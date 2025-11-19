package com.example.final_exam;

public class Workout {
    public String type;
    public int durationMinutes;
    public String imageUrl;
    public long timestamp;

    public Workout() {}

    public Workout(String type, int durationMinutes, String imageUrl, long timestamp) {
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }
}
