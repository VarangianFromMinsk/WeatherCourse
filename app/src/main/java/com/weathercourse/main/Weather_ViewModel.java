package com.weathercourse.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Di.Common_AppComponent;
import com.weathercourse.main.Di.Common_AppModule;

import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;
import com.weathercourse.main.Services.Work_UpdateByInterval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class Weather_ViewModel extends AndroidViewModel {

    private MutableLiveData<String> getCurrentTown = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEditMode = new MutableLiveData<>();

    //for repository
    private LiveData<Forecast_Request> liveForecastRequest = new MutableLiveData<>();
    private LiveData<Weather_Response> liveCurrentWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNetworkErrorExist = new MutableLiveData<>();

    private LiveData<List<Log_Model>> logsLive = new MutableLiveData<>();


    @Inject
    public Weather_ViewModel(@NonNull Application application) {
        super(application);

    }

    //Worker
    public void startWorker(int fullTime) {

        //delete work
        try {
            WorkManager.getInstance().cancelAllWorkByTag("backUpdate");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ErrorInDeleteWork", "work didnt delete");
        }

        //create new work

        //conditions
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build();

        //new work
        PeriodicWorkRequest myWorkInBackground = new PeriodicWorkRequest.Builder(Work_UpdateByInterval.class, fullTime, TimeUnit.MINUTES)
                .addTag("backUpdate")
                .setConstraints(constraints)
                .build();

        WorkManager mWorkManager = WorkManager.getInstance();
        mWorkManager.enqueue(myWorkInBackground);

        WorkManager.getInstance().getWorkInfoByIdLiveData(myWorkInBackground.getId()).observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d("BackWorkInfo", "onChanged: " + Arrays.toString(WorkInfo.State.values()));
                    }
                });
    }

    //town
    public void setCurrentTown(String town){
        getCurrentTown.setValue(town);
    }

    public MutableLiveData<String> getCurrentTown() {
        return getCurrentTown;
    }

    //edit mode
    public void changeEditMode(boolean state){
        isEditMode.setValue(state);
    }

    public MutableLiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }

    //load data
    public void loadAllData(String city){
        Weather_Repository.instance.loadWeather(city);
    }

    //forecast
    public void loadForecast(Context context){
        Weather_Repository.instance.loadForecast(context);
    }


    public LiveData<Forecast_Request> getLiveForecastRequest() {
        liveForecastRequest = Weather_Repository.instance.getLiveForecast();
        return liveForecastRequest;
    }

    //current weather
    public void loadCurrentWeather(Context context){
        Weather_Repository.instance.loadCurrentWeather(context);
    }

    public LiveData<Weather_Response> getLiveCurrentWeather() {
        liveCurrentWeather = Weather_Repository.instance.getLiveCurrentWeather();
        return liveCurrentWeather;
    }

    //internet
    public MutableLiveData<Boolean> getIsNetworkErrorExist() {
        isNetworkErrorExist = Weather_Repository.instance.getIsNetworkErrorExist();
        return isNetworkErrorExist;
    }

    public void setIsNetworkErrorExist(boolean state){
        Weather_Repository.instance.setInternet(state);
    }

    //database
    public void loadLogsFromRoom(){
        Weather_Repository.instance.loadLogsFromRoom(getApplication());
    }


    //logs
    public void itsFirstOpen(){

    }


    public void insertLogInRoom(String reason){
        Weather_Repository.instance.insertLog(reason);
    }



    public LiveData<List<Log_Model>> getLogsLive() {
        logsLive = Weather_Repository.instance.getLogsLive();
        return logsLive;
    }

    //clear log list
    public void clearLogList(){
        Weather_Repository.instance.deleteAllLogs();
    }

}
