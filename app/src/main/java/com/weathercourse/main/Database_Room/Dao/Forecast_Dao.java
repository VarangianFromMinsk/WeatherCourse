package com.weathercourse.main.Database_Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;

import java.util.List;

@Dao
public interface Forecast_Dao {

    @Query("SELECT * FROM forecast")
    LiveData<Forecast_Request> getListForecast();

    @Insert
    void insertNote(Forecast_Request forecast_request);

    @Query("DELETE FROM forecast")
    void deleteForecast();
}
