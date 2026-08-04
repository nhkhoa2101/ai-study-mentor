package com.example.aistudymentor.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.RecentActivity;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {

    private List<RecentActivity> activityList;

    public RecentActivityAdapter(List<RecentActivity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        RecentActivity activity = activityList.get(position);
        holder.tvActivityTitle.setText(activity.getTitle());
        holder.tvActivityDescription.setText(activity.getDescription());
        
        // Format timestamp simply to show time for now (e.g., getting just HH:mm)
        String timeStr = activity.getTimestamp();
        if (timeStr != null && timeStr.length() > 16) {
            timeStr = timeStr.substring(11, 16);
        }
        holder.tvActivityTime.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        return activityList == null ? 0 : activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityTitle, tvActivityDescription, tvActivityTime;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvActivityDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvActivityTime = itemView.findViewById(R.id.tvActivityTime);
        }
    }
}
