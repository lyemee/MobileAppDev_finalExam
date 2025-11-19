package com.example.final_exam;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Random;

public class AddActivity extends AppCompatActivity {

    private Spinner spnType;
    private EditText etDuration, etImageUrl;
    private ImageView ivPreview;

    private static final String[] TYPES = new String[]{
            "Arms","Legs","Back","Walking","Running","Cycling","Swimming","Yoga","HIIT"
    };

    private static final String[] RANDOM_IMAGES = new String[]{
            "https://picsum.photos/seed/workout1/600/400",
            "https://picsum.photos/seed/workout2/600/400",
            "https://picsum.photos/seed/workout3/600/400",
            "https://picsum.photos/seed/workout4/600/400",
            "https://picsum.photos/seed/workout5/600/400"
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        spnType = findViewById(R.id.spnType);
        etDuration = findViewById(R.id.etDuration);
        etImageUrl = findViewById(R.id.etImageUrl);
        ivPreview = findViewById(R.id.ivPreview);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, TYPES);
        spnType.setAdapter(adapter);

        Button btnLoad = findViewById(R.id.btnLoad);
        Button btnRandom = findViewById(R.id.btnRandom);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        btnLoad.setOnClickListener(v -> loadPreview(etImageUrl.getText().toString().trim()));
        btnRandom.setOnClickListener(v -> {
            String url = RANDOM_IMAGES[new Random().nextInt(RANDOM_IMAGES.length)];
            etImageUrl.setText(url);
            loadPreview(url);
        });

        btnSave.setOnClickListener(v -> {
            String type = (String) spnType.getSelectedItem();
            String durStr = etDuration.getText().toString().trim();
            String url = etImageUrl.getText().toString().trim();

            if (TextUtils.isEmpty(durStr)) {
                etDuration.setError("Required");
                return;
            }
            int minutes;
            try {
                minutes = Integer.parseInt(durStr);
                if (minutes <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                etDuration.setError("Enter a positive number");
                return;
            }

            // URL optional; save even if empty (clicking on main will show "No image" toast)
            Workout w = new Workout(type, minutes, url, System.currentTimeMillis());
            Storage.saveWorkout(this, w);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish(); // Back to MainActivity
        });

        btnBack.setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            // Glide will re-load by URL if present
            String url = etImageUrl.getText().toString().trim();
            if (!url.isEmpty()) loadPreview(url);
        }
    }

    private void loadPreview(String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Enter an image URL or tap Random", Toast.LENGTH_SHORT).show();
            return;
        }
        Glide.with(this)
                .load(url)
                .timeout(8000) // graceful timeout
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_delete)
                .into(ivPreview);
    }
}

