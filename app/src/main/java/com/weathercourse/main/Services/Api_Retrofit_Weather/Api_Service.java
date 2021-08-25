package com.weathercourse.main.Services.Api_Retrofit_Weather;


import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api_Service {
    //example "weather?q=Minsk&appid=93cc3b4fae08e22f2504523de02a6f20&units=metric"
    @GET("weather")
    Observable <Weather_Response> getWeather(@Query("appid") String apiKey, @Query("q") String city,@Query("lang") String lang, @Query("units") String type);

    //forecast?q=Minsk&appid=93cc3b4fae08e22f2504523de02a6f20&lang=en&units=metric
    @GET("forecast")
    Observable<Forecast_Request> getForecast(@Query("appid") String apiKey, @Query("q") String city,@Query("lang") String lang, @Query("units") String type);

}
