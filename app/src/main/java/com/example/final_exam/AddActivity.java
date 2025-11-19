package com.example.final_exam;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images.jpg",
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images%20(1).jpg",
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images%20(2).jpg",
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images%20(3).jpg",
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images%20(4).jpg",
            "https://raw.githubusercontent.com/lyemee/MobileAppDev_finalExam/main/images%20(5).jpg"
    };

    private static final String KEY_TYPE_POS = "key_type_pos";
    private static final String KEY_DURATION = "key_duration";
    private static final String KEY_IMAGE_URL = "key_image_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

            Workout w = new Workout(type, minutes, url, System.currentTimeMillis());
            Storage.saveWorkout(this, w);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish(); // Back to MainActivity
        });

        btnBack.setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt(KEY_TYPE_POS, 0);
            spnType.setSelection(pos);

            String dur = savedInstanceState.getString(KEY_DURATION, "");
            etDuration.setText(dur);

            String url = savedInstanceState.getString(KEY_IMAGE_URL, "");
            etImageUrl.setText(url);
            if (!TextUtils.isEmpty(url)) {
                loadPreview(url);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TYPE_POS, spnType.getSelectedItemPosition());
        outState.putString(KEY_DURATION, etDuration.getText().toString().trim());
        outState.putString(KEY_IMAGE_URL, etImageUrl.getText().toString().trim());
    }

    private void loadPreview(String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Enter an image URL or tap Random", Toast.LENGTH_SHORT).show();
            return;
        }
        Glide.with(this)
                .load(url)
                .timeout(8000)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_delete)
                .into(ivPreview);
    }
}
