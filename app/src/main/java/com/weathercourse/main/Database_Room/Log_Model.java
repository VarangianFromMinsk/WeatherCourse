package com.weathercourse.main.Database_Room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "logs")
public class Log_Model {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String time,reason;

    public Log_Model(int id, String time, String reason) {
        this.id = id;
        this.time = time;
        this.reason = reason;
    }

    @Ignore
    public Log_Model(String time, String reason) {
        this.time = time;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
