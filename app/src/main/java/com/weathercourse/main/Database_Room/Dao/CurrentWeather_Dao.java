package com.weathercourse.main.Database_Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.weathercourse.main.Services.Pojo_Current.Weather_Response;

@Dao
public interface CurrentWeather_Dao {

    @Query("SELECT * FROM currentWeather")
    LiveData<Weather_Response> getCurrentWeather();

    @Insert
    void insertNote(Weather_Response weatherResponse);

    @Query("DELETE FROM currentWeather")
    void deleteAllInfoCurrentWeather();

}
