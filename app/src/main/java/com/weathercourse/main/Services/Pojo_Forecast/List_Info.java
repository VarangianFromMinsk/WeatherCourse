package com.weathercourse.main.Services.Pojo_Forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List_Info {

    @SerializedName("dt")
    @Expose
    private int dt;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = null;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("visibility")
    @Expose
    private int visibility;
    @SerializedName("pop")
    @Expose
    private float pop;
    @SerializedName("rain")
    @Expose
    private Rain rain;
    @SerializedName("sys")
    @Expose
    private Sys sys;
    @SerializedName("dt_txt")
    @Expose
    private String dtTxt;

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public float getPop() {
        return pop;
    }

    public void setPop(float pop) {
        this.pop = pop;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }

}
