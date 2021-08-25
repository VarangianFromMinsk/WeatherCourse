package com.weathercourse.main.Database_Room.Converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weathercourse.main.Services.Pojo_Current.Main;
import com.weathercourse.main.Services.Pojo_Current.Weather;
import com.weathercourse.main.Services.Pojo_Forecast.City;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converter_Forecast {

    // list<ListInfo>
    @TypeConverter
    public String listInfoToString(List<List_Info> list){
        return new Gson().toJson(list);
    }

    @TypeConverter
    public List<List_Info> stringToListInfo(String stringData){

        Gson gson = new Gson();
        if (stringData == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<List_Info>>() {}.getType();
        return gson.fromJson(stringData, listType);

    }

    //City
    @TypeConverter
    public String cityToString(City city){
        return new Gson().toJson(city);
    }

    @TypeConverter
    public City stringToCity(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, City.class);
    }
}
