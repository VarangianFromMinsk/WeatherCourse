package com.weathercourse.main.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Factory;
import com.weathercourse.main.Services.Api_Retrofit_Weather.Api_Service;
import com.weathercourse.main.Services.App_Constants;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;
import com.weathercourse.main.MainActivity;
import com.weathercourse.main.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WeatherWidget extends AppWidgetProvider {

    private static Disposable disposableForecast;
    private static Logs_DataBase dataBase;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        Api_Factory api_factory = Api_Factory.getInstance();
        Api_Service api_service = api_factory.getApiService();
        dataBase = Logs_DataBase.getInstance(context);

        disposableForecast = api_service.getForecast(App_Constants.API_KEY_WEATHER, "Minsk","en", "metric" )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Forecast_Request>() {
                    @Override
                    public void accept(Forecast_Request forecast_request) throws Exception {

                        List<List_Info> listFromServer= forecast_request.getList().subList(0,3);
                        ArrayList<List_Info> list = new ArrayList<>(listFromServer);


                        //update base
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                dataBase.forecast_dao().deleteForecast();
                                dataBase.forecast_dao().insertNote(forecast_request);
                            }
                        });


                        if(!list.isEmpty()){
                            String city = forecast_request.getCity().getName();
                            int currentTemp = getTemp(0, list);
                            views.setTextViewText(R.id.tempWidget, city);
                            views.setTextViewText(R.id.tempWidgetValue, String.valueOf(currentTemp + "\u00B0"));

                            views.setViewVisibility(R.id.noInternetUpdate, View.GONE);

                            try {
                                //1st colon
                                int firstTemp = getTemp(0, list);
                                String firstHour = getHour(0,list);
                                String firstType = getType(0,list);
                                int imageResource = getIconByType(firstType, false);

                                views.setTextViewText(R.id.firstColon,firstHour);
                                views.setTextViewText(R.id.firstColonTemp, firstTemp + "\u00B0");
                                views.setImageViewResource(R.id.firstColonIv, imageResource);

                                //2nd colon
                                int secondTemp = getTemp(1, list);
                                String secondHour = getHour(1,list);
                                String secondType = getType(1,list);
                                int imageResourceSecond = getIconByType(secondType, false);

                                views.setTextViewText(R.id.secondColon,secondHour);
                                views.setTextViewText(R.id.secondColonTemp, secondTemp + "\u00B0");
                                views.setImageViewResource(R.id.secondColonIv, imageResourceSecond);

                                //3th colon
                                int thirdTemp = getTemp(2, list);
                                String thirdHour = getHour(2,list);
                                String thirdType = getType(2,list);
                                int imageResourceThird = getIconByType(thirdType, false);

                                views.setTextViewText(R.id.thirdColon, thirdHour);
                                views.setTextViewText(R.id.thirdColonTemp, thirdTemp + "\u00B0");
                                views.setImageViewResource(R.id.thirdColonIv, imageResourceThird);

                                int imageResourceOfBackground = getIconByType(firstType, true);
                                views.setImageViewResource(R.id.widgetBigBackground, imageResourceOfBackground);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("retrofitError", String.valueOf(e));
                            }


                        }
                        else{
                            views.setViewVisibility(R.id.noInternetUpdate, View.VISIBLE);
                            Intent showAppIntent = new Intent(context, MainActivity.class);
                            PendingIntent showAppPendingIntent = PendingIntent.getActivity(context, 1, showAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            views.setOnClickPendingIntent(R.id.noInternetUpdate, showAppPendingIntent);
                        }


                        commonUpdateWidgetPart(context,appWidgetId,views,appWidgetManager,true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        views.setViewVisibility(R.id.noInternetUpdate, View.VISIBLE);
                        Intent showAppIntent = new Intent(context, MainActivity.class);
                        PendingIntent showAppPendingIntent = PendingIntent.getActivity(context, 1, showAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        views.setOnClickPendingIntent(R.id.noInternetUpdate, showAppPendingIntent);

                        commonUpdateWidgetPart(context,appWidgetId,views,appWidgetManager, false);
                    }
                });

        commonUpdateWidgetPart(context,appWidgetId,views,appWidgetManager,false);
    }


    public static int getTemp(int position, ArrayList<List_Info> list){
        return (int) list.get(position).getMain().getTemp();
    }

    public static String getHour(int position, ArrayList<List_Info> list){
        return list.get(position).getDtTxt().substring(11,13);
    }

    public static String getType(int position, ArrayList<List_Info> list){
        return list.get(position).getWeather().get(0).getDescription();
    }

    public static int getIconByType(String type, boolean isThatBigImage){
        if(type.contains("sky")){
            //clear sly
            if(!isThatBigImage){
                return R.drawable.weather_sky;
            }
            else{
                return  R.drawable.back_main_sunny;
            }
        }
        else if(type.contains("clouds")){
            if(!isThatBigImage){
                return R.drawable.weather_cloudy;
            }
            else{
                return  R.drawable.back_main_cloudy;
            }
        }
        else if(type.contains("rain") || type.contains("drizzle")){
            if(!isThatBigImage){
                return R.drawable.weather_rain;
            }
            else{
                return R.drawable.back_main_rain;
            }
        }
        else if(type.contains("thunderstorm")){
            if(!isThatBigImage){
                return R.drawable.weather_thunder;
            }
            else{
                return R.drawable.back_main_thunder;
            }
        }
        else if(type.contains("snow") || type.contains("shower")){
            if(!isThatBigImage){
                return R.drawable.weather_snow;
            }
            else{
                return R.drawable.back_main;
            }
        }
        else if(type.contains("mist") || type.contains("fog")){
            if(!isThatBigImage){
                return R.drawable.weather_fog;
            }
            else{
                return R.drawable.back_main_mist;
            }
        }
        else{
            //default
            if(!isThatBigImage){
                return R.drawable.weather_default;
            }
            else{
                return R.drawable.back_main;
            }
        }
    }

    public static void commonUpdateWidgetPart(Context context, int appWidgetId, RemoteViews views, AppWidgetManager appWidgetManager, boolean isUpdateBtn){
        Intent updateIntent = new Intent(context, WeatherWidget.class);
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingUpdateIntent = PendingIntent.getActivity(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.updateWidgetBtn, pendingUpdateIntent);

        if(isUpdateBtn){
            //update base
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //update log
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault());
                    String saveCurrentDate = currentDate.format(calendar.getTime());

                    dataBase.log_dao().insertNote(new Log_Model(saveCurrentDate,"Widget"));
                }
            });
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        if (disposableForecast != null) {
            disposableForecast .dispose();
        }
    }
}