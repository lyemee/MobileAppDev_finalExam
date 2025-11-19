package com.example.final_exam;

import android.app.Dialog;
import android.os.Bundle;
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

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        ImageView iv = new ImageView(requireContext());
        iv.setAdjustViewBounds(true);
        int pad = (int)(16 * requireContext().getResources().getDisplayMetrics().density);
        iv.setPadding(pad, pad, pad, pad);
        dialog.setContentView(iv);

        String url = getArguments() != null ? getArguments().getString(ARG_URL) : null;
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No image URL", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Glide.with(iv).load(url)
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_delete)
                    .into(iv);
        }
        return dialog;
    }
}
