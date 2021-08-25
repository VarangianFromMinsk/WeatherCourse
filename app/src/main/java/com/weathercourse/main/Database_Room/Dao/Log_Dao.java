package com.weathercourse.main.Database_Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.weathercourse.main.Database_Room.Log_Model;

import java.util.List;

@Dao
public interface Log_Dao {

    @Query("SELECT * FROM logs")
    LiveData<List<Log_Model>> getAllLog();

    @Insert
    void insertNote(Log_Model log);

    @Query("DELETE FROM logs")
    void deleteAllSongs();


}
