package com.example.final_exam;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class ImageDialog extends DialogFragment {

    private static final String ARG_URL = "url";

    public static ImageDialog newInstance(String url) {
        ImageDialog d = new ImageDialog();
        Bundle b = new Bundle();
        b.putString(ARG_URL, url);
        d.setArguments(b);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(STYLE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));

        FrameLayout container = new FrameLayout(requireContext());
        container.setPadding(32, 32, 32, 32);

        ImageView imageView = new ImageView(requireContext());
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setBackgroundColor(Color.WHITE); // 카드 배경 느낌

        container.addView(imageView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        ));

        dialog.setContentView(container);

        container.setOnClickListener(v -> dismiss());

        String url = getArguments().getString(ARG_URL);
        if (url == null) {
            Toast.makeText(getContext(), "No image", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Glide.with(requireContext())
                    .load(url)
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_delete)
                    .into(imageView);
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.85);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }
    }
}
