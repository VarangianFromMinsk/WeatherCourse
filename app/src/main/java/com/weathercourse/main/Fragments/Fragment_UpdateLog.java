package com.weathercourse.main.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.weathercourse.main.Adapters.Recycler_AdapterTimeLog;
import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Database_Room.Logs_DataBase;
import com.weathercourse.main.Di.Common_AppModule;
import com.weathercourse.main.Di.DaggerCommon_AppComponent;
import com.weathercourse.main.Di.ViewModelFactory;
import com.weathercourse.main.R;
import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Weather_ViewModel;
import com.weathercourse.main.databinding.FragmentUpdateLogBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class Fragment_UpdateLog extends Fragment {

    private FragmentUpdateLogBinding binding;
    private String city;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Recycler_AdapterTimeLog adapterTimeLog;

    @Inject
    ViewModelFactory viewModelFactory;
    Weather_ViewModel viewModel;


    public static Fragment_UpdateLog newInstance(String param1, String param2) {
        Fragment_UpdateLog fragment = new Fragment_UpdateLog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_update_log,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializationAnotherStaff();

        checkWeatherToSetRightBackground();

        initRefreshData();

        seekBarAction();

        initPathToCurrentWeather(view);

        logRecyclerView();

        initClearLog();
    }

    private void initializationAnotherStaff() {


        DaggerCommon_AppComponent.builder().common_AppModule(new Common_AppModule(requireActivity().getApplication())).build().inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(Weather_ViewModel.class);

        city = sharedPreferences.getString("currentCity", "Grodno");
        int currentRange = sharedPreferences.getInt("CurrentRange", 30);

        binding.seekBarInterval.setProgress(currentRange);
        binding.timeTv.setText(String.valueOf(currentRange + " min"));
    }

    private void initRefreshData() {
        binding.refreshChooseInterval.setColorSchemeResources(R.color.purple_500);
        binding.refreshChooseInterval.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadAllData(city);

                viewModel.insertLogInRoom("pull to refresh by list");
                viewModel.changeEditMode(false);
            }
        });
    }

    private void seekBarAction() {

        binding.seekBarInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int usersTime =seekBar.getProgress();
                if(usersTime < 15){
                    binding.seekBarInterval.setProgress(15);
                    usersTime = 15;
                    //start back work
                    viewModel.startWorker(usersTime);
                    sharedPreferences.edit().putInt("CurrentRange", usersTime).apply();
                    Toast.makeText(requireContext(), "Minimum 15 min", Toast.LENGTH_SHORT).show();
                }
                else{
                    sharedPreferences.edit().putInt("CurrentRange", usersTime).apply();
                    Toast.makeText(requireContext(), "Time to undate set", Toast.LENGTH_SHORT).show();
                    //start back work
                    viewModel.startWorker(usersTime);
                }
                binding.timeTv.setText(String.valueOf(usersTime + " min"));
            }
        });
    }

    private void checkWeatherToSetRightBackground() {
        viewModel.getLiveCurrentWeather().observe(getViewLifecycleOwner(), new Observer<Weather_Response>() {
            @Override
            public void onChanged(Weather_Response weather_response) {

                binding.refreshChooseInterval.setRefreshing(false);

                try {
                    //change background
                    if (weather_response.getWeather().get(0).getDescription().contains("sky")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main_sunny);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("clouds")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main_cloudy);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("rain") || weather_response.getWeather().get(0).getDescription().contains("drizzle")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main_rain);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("thunderstorm")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main_thunder);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("snow") || weather_response.getWeather().get(0).getDescription().contains("shower")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("mist") || weather_response.getWeather().get(0).getDescription().contains("fog")) {
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main_mist);
                    } else {
                        //default
                        binding.imageBackgroundTimeLog.setBackgroundResource(R.drawable.back_main);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(requireContext(), "Sorry< something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initPathToCurrentWeather(View view) {
        binding.pathToCurrentWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_fragment_UpdateLog_to_main_Fragment);
            }
        });
    }

    private void logRecyclerView() {
        binding.recyclerViewLog.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapterTimeLog.setLogs(new ArrayList<Log_Model>());
        binding.recyclerViewLog.setAdapter(adapterTimeLog);

        // грузим в фрагменте с основной погодой, т.к. в этот метод вписали инициализацию Базы Данных
       //viewModel.loadLogsFromRoom();

        viewModel.getLogsLive().observe(getViewLifecycleOwner(), new Observer<List<Log_Model>>() {
            @Override
            public void onChanged(List<Log_Model> log_models) {
                ArrayList<Log_Model> list = new ArrayList<>(log_models);
                adapterTimeLog.setLogs(list);
            }
        });
    }

    private void initClearLog() {
        binding.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clearLogList();
            }
        });
    }
}