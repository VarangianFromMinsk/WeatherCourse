package com.weathercourse.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Factory;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Service;
import com.weathercourse.main.Services.App_Constants;
import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;

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

public class Weather_Repository {

    public static final Weather_Repository instance = new Weather_Repository();


    private LiveData<Forecast_Request> liveForecast = new MutableLiveData<>();
    private LiveData<Weather_Response> liveCurrentWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNetworkErrorExist = new MutableLiveData<>();

    private ArrayList<List_Info> checkListForNet = new ArrayList<>();

    private static Logs_DataBase dataBase;
    private LiveData<List<Log_Model>> logsLive = new MutableLiveData<>();

    private final Api_Factory api_factory = Api_Factory.getInstance();
    private final Api_Service api_service = api_factory.getApiService();

    //load forecast
    public void loadWeather(String city) {

        Disposable disposableCurrentWeather = api_service.getWeather(App_Constants.API_KEY_WEATHER, city, "en", "metric")
                .subscribeOn(Schedulers.io())  // TODO: в каком потоке выполняем
                .observeOn(AndroidSchedulers.mainThread())  // TODO: в какой поток возвращаем
                .subscribe(new Consumer<Weather_Response>() {
                    @Override
                    public void accept(Weather_Response weather_response) throws Exception {

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {

                                dataBase.currentWeather_dao().deleteAllInfoCurrentWeather();
                                dataBase.currentWeather_dao().insertNote(weather_response);
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

                        isNetworkErrorExist.setValue(false);

                        checkListForNet.clear();
                        List<List_Info> list = forecast_request.getList();
                        checkListForNet.addAll(list);


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
                        Log.d("retrofitError", String.valueOf(throwable));
                        try {
                            if (checkListForNet.isEmpty()) {
                                isNetworkErrorExist.setValue(true);
                            }
                        } catch (Exception e) {
                            isNetworkErrorExist.setValue(true);
                            e.printStackTrace();
                        }
                    }
                });


    }

    //forecast
    public void loadForecast(Context context){
        dataBase = Logs_DataBase.getInstance(context);
        liveForecast = dataBase.forecast_dao().getListForecast();
    }


    public LiveData<Forecast_Request> getLiveForecast() {
        return liveForecast;
    }

    //load current weather
    public void loadCurrentWeather(Context context){
        dataBase = Logs_DataBase.getInstance(context);
        liveCurrentWeather = dataBase.currentWeather_dao().getCurrentWeather();
    }


    public LiveData<Weather_Response> getLiveCurrentWeather() {
        return liveCurrentWeather;
    }

    //network
    public MutableLiveData<Boolean> getIsNetworkErrorExist() {
        return isNetworkErrorExist;
    }

    //logs
    public void loadLogsFromRoom(Context context) {
        dataBase = Logs_DataBase.getInstance(context);
        logsLive = dataBase.log_dao().getAllLog();

    }

    public void insertLog(String reason) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault());
                String saveCurrentDate = currentDate.format(calendar.getTime());

                dataBase.log_dao().insertNote(new Log_Model(saveCurrentDate, reason));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread
                        //  loadLogsFromRoom(context);
                    }
                });
            }
        });
    }

    public void deleteAllLogs() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                dataBase.log_dao().deleteAllSongs();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread
                        //  loadLogsFromRoom(context);
                    }
                });
            }
        });
    }

    public LiveData<List<Log_Model>> getLogsLive() {
        return logsLive;
    }
}
