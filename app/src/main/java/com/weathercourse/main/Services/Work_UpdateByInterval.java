package com.weathercourse.main.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Factory;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Service;
import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;
import com.weathercourse.main.Weather_ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Work_UpdateByInterval extends Worker {

    private final String city;
    private final Context context;

    private final Api_Factory api_factory = Api_Factory.getInstance();
    private final Api_Service api_service = api_factory.getApiService();

    private static Logs_DataBase dataBase;


    public Work_UpdateByInterval(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d("CheckWorker", String.valueOf(true));

        this.context = context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        city = sharedPreferences.getString("currentCity", "Grodno");

        dataBase = Logs_DataBase.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {

        //update weather
        updateWeatherInDatabase();

       // context.sendBroadcast(new Intent("WeatherCourse_Update"));

        return Result.success();
    }

    private void updateWeatherInDatabase() {
        Disposable disposableCurrentWeather = api_service.getWeather(App_Constants.API_KEY_WEATHER, city, "en", "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Weather_Response>() {
                    @Override
                    public void accept(Weather_Response weather_response) throws Exception {

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                dataBase.currentWeather_dao().deleteAllInfoCurrentWeather();
                                dataBase.currentWeather_dao().insertNote(weather_response);

                                //update log
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault());
                                String saveCurrentDate = currentDate.format(calendar.getTime());

                                dataBase.log_dao().insertNote(new Log_Model(saveCurrentDate,"workManager"));
                            }
                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("retrofitError", String.valueOf(throwable));
                    }
                });

        Disposable disposableForecast = api_service.getForecast(App_Constants.API_KEY_WEATHER, city, "en", "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Forecast_Request>() {
                    @Override
                    public void accept(Forecast_Request forecast_request) throws Exception {

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                dataBase.forecast_dao().deleteForecast();
                                dataBase.forecast_dao().insertNote(forecast_request);
                            }
                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("retrofitErrorWorker", String.valueOf(throwable));
                    }
                });
    }
}
