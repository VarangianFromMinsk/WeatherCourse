package com.weathercourse.main.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.R;

import java.util.ArrayList;

public class Recycler_AdapterTimeLog extends RecyclerView.Adapter<Recycler_AdapterTimeLog.LogViewHolder> {

    private ArrayList<Log_Model> logs = new ArrayList<>();


    public void setLogs(ArrayList<Log_Model> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log_Model log = logs.get(position);
        holder.logTv.setText(String.valueOf(log.getTime() + " / " + log.getReason()));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }


    public static class LogViewHolder extends RecyclerView.ViewHolder {

        private final TextView logTv;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            logTv = itemView.findViewById(R.id.logTv);
        }
    }

}
