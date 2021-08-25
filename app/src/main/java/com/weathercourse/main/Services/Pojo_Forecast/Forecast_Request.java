package com.weathercourse.main.Services.Pojo_Forecast;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weathercourse.main.Database_Room.Converters.Converter_CurrentWeather;
import com.weathercourse.main.Database_Room.Converters.Converter_Forecast;

import java.util.List;

@Entity(tableName = "forecast")
@TypeConverters(value = Converter_Forecast.class)
public class Forecast_Request {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private int message;
    @SerializedName("cnt")
    @Expose
    private int cnt;
    @SerializedName("list")
    @Expose
    private List<List_Info> list = null;
    @SerializedName("city")
    @Expose
    private City city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public java.util.List<List_Info> getList() {
        return list;
    }

    public void setList(java.util.List<List_Info> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
