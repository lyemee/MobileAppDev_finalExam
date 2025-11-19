package com.example.final_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.VH> {

    public interface OnItemClick {
        void onClick(Workout w);
    }

    private final List<Workout> items;
    private final OnItemClick click;

    public RecentAdapter(List<Workout> items, OnItemClick click) {
        this.items = items;
        this.click = click;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Workout w = items.get(pos);
        h.type.setText(w.type);
        h.duration.setText(w.durationMinutes + " min");
        h.itemView.setOnClickListener(v -> click.onClick(w));
    }

    @Override public int getItemCount() { return items.size(); }

    public void setItems(List<Workout> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }


    static class VH extends RecyclerView.ViewHolder {
        TextView type, duration;
        VH(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.tvType);
            duration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
