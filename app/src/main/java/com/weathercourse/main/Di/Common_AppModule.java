package com.weathercourse.main.Di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.weathercourse.main.Adapters.Recycler_AdapterForecast;
import com.weathercourse.main.Adapters.Recycler_AdapterTimeLog;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Weather_ViewModel;


import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class Common_AppModule {

    Application application;
    Activity activity;
    Context context;
    SharedPreferences preferences;

    public Common_AppModule(Application application) {
        this.application = application;
        activity = (Activity) context;
        context = application.getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //todo: помечаем что эти методы и есть каие-то нужные нам зависимости
    @Provides
    @Singleton
    Context provideContext(){
        return this.context;
    }

    @Provides
    @Singleton
    Application provideApplication(){
        return this.application;
    }

    @Provides
    @Singleton
    Activity provideActivity(){
        return this.activity;
    }

    @Provides
    @Singleton
    SharedPreferences providePreference(){
        return this.preferences;
    }


    @Provides
    @Singleton
    Recycler_AdapterTimeLog provideAdapterTimeLog(){
        return new Recycler_AdapterTimeLog();
    }

    @Provides
    @Singleton
    Recycler_AdapterForecast provideAdapterForecast(Context context){
        return new Recycler_AdapterForecast(context);
    }

    @Provides
    LinearLayoutManager provideLinearLayout(Context context){
        return new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Provides
    Logs_DataBase provideDatabase(Context context){
        return Logs_DataBase.getInstance(context);
    }

}
