package com.weathercourse.main.Database_Room.Converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weathercourse.main.Services.Pojo_Current.Clouds;
import com.weathercourse.main.Services.Pojo_Current.Coord;
import com.weathercourse.main.Services.Pojo_Current.Main;
import com.weathercourse.main.Services.Pojo_Current.Sys;
import com.weathercourse.main.Services.Pojo_Current.Weather;
import com.weathercourse.main.Services.Pojo_Current.Wind;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converter_CurrentWeather {

    // list<Weather>
    @TypeConverter
    public String listCurrentWeatherToString(List<Weather> list){

       return new Gson().toJson(list);
//        JSONArray jsonArray = new JSONArray();
//        for(Weather weatherItem : list){
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("id", weatherItem.getId());
//                jsonObject.put("main", weatherItem.getMain());
//                jsonObject.put("description", weatherItem.getDescription());
//                jsonObject.put("icon", weatherItem.getIcon());
//
//                jsonArray.put(jsonObject);
//            } catch (JSONException e) {
//                Log.d("JsonConverter", "error in current weather");
//                e.printStackTrace();
//            }
//        }
//        return jsonArray.toString();
    }

    @TypeConverter
    public List<Weather> stringCurrentWeatherToList(String stringData){

        Gson gson = new Gson();
        if (stringData == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Weather>>() {}.getType();
        return gson.fromJson(stringData, listType);
    }

    //Coord
    @TypeConverter
    public String coordToString(Coord coord){
        return new Gson().toJson(coord);
    }

    @TypeConverter
    public Coord stringToCoord(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, Coord.class);
    }


    //Main
    @TypeConverter
    public String mainToString(Main main){
        return new Gson().toJson(main);
    }

    @TypeConverter
    public Main stringToMain(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, Main.class);
    }

    //Wind
    @TypeConverter
    public String windToString(Wind wind){
        return new Gson().toJson(wind);
    }

    @TypeConverter
    public Wind stringToWind(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, Wind.class);
    }

    //clouds
    @TypeConverter
    public String cloudsToString(Clouds clouds){
        return new Gson().toJson(clouds);
    }

    @TypeConverter
    public Clouds stringToClouds(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, Clouds.class);
    }

    //sys
    @TypeConverter
    public String sysToString(Sys sys){
        return new Gson().toJson(sys);
    }

    @TypeConverter
    public Sys stringToSys(String stringData){
        Gson gson = new Gson();
        return gson.fromJson(stringData, Sys.class);
    }

}
