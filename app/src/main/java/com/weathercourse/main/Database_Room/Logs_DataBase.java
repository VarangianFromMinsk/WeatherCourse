package com.weathercourse.main.Database_Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.weathercourse.main.Database_Room.Dao.CurrentWeather_Dao;
import com.weathercourse.main.Database_Room.Dao.Forecast_Dao;
import com.weathercourse.main.Database_Room.Dao.Log_Dao;
import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;

@Database(entities = {Log_Model.class, Weather_Response.class, Forecast_Request.class} , version = 9 , exportSchema = false)
public abstract class Logs_DataBase extends RoomDatabase {

    private static Logs_DataBase database;

    private static final String DB_NAME = "weather_db";
    private static final Object LOCK = new Object();

    //TODO: ленивый сигнлтон, но с синхронайз ( дает потокобезопасноть, но достаточно медленно)
    public static Logs_DataBase getInstance(Context context){
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, Logs_DataBase.class, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return database;
    }

    public abstract Log_Dao log_dao();

    public abstract CurrentWeather_Dao currentWeather_dao();

    public abstract Forecast_Dao forecast_dao();

}
