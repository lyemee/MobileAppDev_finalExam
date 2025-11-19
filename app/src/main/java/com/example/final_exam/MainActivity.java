package com.example.final_exam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private TextView tvStepCount;
    private RecyclerView rv;
    private RecentAdapter adapter;
    private List<Workout> all = new ArrayList<>();

    private final ActivityResultLauncher<String> permRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Toast.makeText(this, "Activity recognition denied. Steps unavailable.", Toast.LENGTH_LONG).show();
                } else {
                    startStepListening();
                }
            });

    private String todayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStepCount = findViewById(R.id.tvStepCount);
        rv = findViewById(R.id.rvRecent);
        rv.setLayoutManager(new LinearLayoutManager(this));

        all = Storage.loadWorkouts(this);
        updateList();

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
    }

    private void updateList() {
        List<Workout> sorted = new ArrayList<>(all);

        sorted.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));

        List<Workout> toShow = sorted.size() > 5 ? sorted.subList(0, 5) : sorted;
        if (adapter == null) {
            adapter = new RecentAdapter(toShow, w ->
                    ImageDialog.newInstance(w.imageUrl).show(getSupportFragmentManager(), "img")
            );
            rv.setAdapter(adapter);
        } else {
            adapter.setItems(toShow);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        all = Storage.loadWorkouts(this);
        updateList();
        ensureStepPermissionAndStart();
    }

    @Override protected void onPause() {
        super.onPause();
        stopStepListening();
    }

    private void ensureStepPermissionAndStart() {
        if (stepCounter == null) {
            tvStepCount.setText("No step sensor");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                permRequest.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                return;
            }
        }
        startStepListening();
    }

    private void startStepListening() {
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopStepListening() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String key = todayKey();

        String savedDate = Storage.getSavedDate(this);
        if (!key.equals(savedDate)) {
            Storage.setStepBaseline(this, key, event.values[0]);
        }

        float baseline = Storage.getStepBaseline(this, key);
        if (Float.isNaN(baseline)) {
            Storage.setStepBaseline(this, key, event.values[0]);
            baseline = event.values[0];
        }
        int todaySteps = Math.max(0, Math.round(event.values[0] - baseline));
        tvStepCount.setText(String.valueOf(todaySteps));
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
}

