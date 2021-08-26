package com.weathercourse.main.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weathercourse.main.Di.Common_AppModule;

import com.weathercourse.main.Di.DaggerCommon_AppComponent;
import com.weathercourse.main.Di.ViewModelFactory;
import com.weathercourse.main.Services.Pojo_Current.Weather;
import com.weathercourse.main.Services.Pojo_Current.Weather_Response;
import com.weathercourse.main.Services.Pojo_Forecast.Forecast_Request;
import com.weathercourse.main.Services.Pojo_Forecast.List_Info;
import com.weathercourse.main.R;
import com.weathercourse.main.Adapters.Recycler_AdapterForecast;
import com.weathercourse.main.Services.WeatherUpdateReceiver;
import com.weathercourse.main.Weather_ViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class Main_Fragment extends Fragment {

    //Data binding don't work in Grid layout

    private TextView tempTv, weatherDescriptionTv, humidityTv, cityTv, feelsLikeTv,
            countryTv, sunsetTv, windTv, pressureTv, windGust, visibilityTv, checkYourInternetConnection;
    private EditText changeCurrentTown;
    private View additionalLineToRecycler;
    private ImageButton editCityBtn, closeBtn;
    private ImageView background, viewInstruction;
    private MaterialButton skipBtn;
    private RelativeLayout instructionLay;
    private RecyclerView recyclerView;
    private String city;
    private boolean isUserSawInstructions;
    private SwipeRefreshLayout refreshLayout;
    private Button logFragmentBtn;

    private Boolean isItAppOpenAction;


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    LinearLayoutManager linearLayoutManager;

    @Inject
    Recycler_AdapterForecast adapter;

    @Inject
    ViewModelFactory viewModelFactory;
    Weather_ViewModel viewModel;


    // TODO: Rename and change types and number of parameters
    public static Main_Fragment newInstance(String param1, String param2) {
        Main_Fragment fragment = new Main_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializationView(view);

        initializationAnotherStaff();

        instructionAction();

        backBtnAction(view);

        initEditCityBtn();

        initEditTextChangeCity();

        initRefreshData();

        initPathToLogUpdateWeather(view);

        actionChange();

        getAndSetMainInfoAction();

        getInfoAndInitForecastRecycler();

        NetworkDisableAction();

        loadWeather(city);

        broadCastInternet();

        broadCastWorker();

    }

    private void initializationView(View view) {

        background = view.findViewById(R.id.imageBackground);

        cityTv = view.findViewById(R.id.cityTv);
        weatherDescriptionTv = view.findViewById(R.id.weatherDescriptionTv);
        tempTv = view.findViewById(R.id.tempTv);

        changeCurrentTown = view.findViewById(R.id.editCityTv);
        recyclerView = view.findViewById(R.id.recyclerView);

        refreshLayout = view.findViewById(R.id.refreshMainInfo);
        logFragmentBtn = view.findViewById(R.id.logFragmentBtn);

        additionalLineToRecycler = view.findViewById(R.id.additionalLineToRecycler);

        editCityBtn = view.findViewById(R.id.editBtn);
        closeBtn = view.findViewById(R.id.closeBtn);

        humidityTv = view.findViewById(R.id.HumidityTV);
        feelsLikeTv = view.findViewById(R.id.Feels_likeTV);
        countryTv = view.findViewById(R.id.CountryTv);
        sunsetTv = view.findViewById(R.id.SunsetTv);
        windTv = view.findViewById(R.id.WindTv);
        pressureTv = view.findViewById(R.id.PressureTV);
        windGust = view.findViewById(R.id.windGustTv);
        visibilityTv = view.findViewById(R.id.VisibilityTv);

        viewInstruction = view.findViewById(R.id.viewInstruction);
        skipBtn = view.findViewById(R.id.skipInstruction);
        instructionLay = view.findViewById(R.id.instructionLay);
        checkYourInternetConnection = view.findViewById(R.id.checkInternet);
    }

    private void initializationAnotherStaff() {

        DaggerCommon_AppComponent.builder().common_AppModule(new Common_AppModule(requireActivity().getApplication())).build().inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(Weather_ViewModel.class);

        viewModel.loadForecast(requireContext());
        viewModel.loadCurrentWeather(requireContext());
        viewModel.loadLogsFromRoom();

        viewModel.insertLogInRoom("app open");

        city = sharedPreferences.getString("currentCity", "Grodno");
        isUserSawInstructions = sharedPreferences.getBoolean("checkInstructions", false);
    }

    private void instructionAction() {
        if (!isUserSawInstructions) {
            instructionLay.setVisibility(View.VISIBLE);
            skipBtn.setVisibility(View.VISIBLE);
            viewInstruction.setVisibility(View.VISIBLE);
            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewInstruction.setImageResource(R.drawable.instruction_widget);
                    skipBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sharedPreferences.edit().putBoolean("checkInstructions", true).apply();
                            instructionLay.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    private void initEditCityBtn() {
        editCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.changeEditMode(true);
            }
        });
    }

    private void initEditTextChangeCity() {
        changeCurrentTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        loadWeather(String.valueOf(s));
                    }
                }, 500);
            }
        });
    }

    private void initRefreshData() {
        refreshLayout.setColorSchemeResources(R.color.purple_500);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                viewModel.insertLogInRoom("pull to refresh main");
                viewModel.changeEditMode(false);
                loadWeather(city);
            }
        });
    }

    private void initPathToLogUpdateWeather(View view) {
        logFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_main_Fragment_to_fragment_UpdateLog);
            }
        });
    }


    private void actionChange() {
        viewModel.getIsEditMode().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean state) {
                if (state) {
                    changeCurrentTown.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.VISIBLE);
                    editCityBtn.setVisibility(View.GONE);
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewModel.changeEditMode(false);
                        }
                    });
                } else {
                    changeCurrentTown.setVisibility(View.GONE);
                    closeBtn.setVisibility(View.GONE);
                    editCityBtn.setVisibility(View.VISIBLE);
                    initEditCityBtn();
                }
            }
        });
    }

    private void backBtnAction(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    requireActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    public void loadWeather(String city_default) {
        viewModel.loadAllData(city_default);
    }

    private void getAndSetMainInfoAction() {

        viewModel.getLiveCurrentWeather().observe(getViewLifecycleOwner(), new Observer<Weather_Response>() {
            @Override
            public void onChanged(Weather_Response weather_response) {

                refreshLayout.setRefreshing(false);

                try {
                    List<Weather> weather = weather_response.getWeather();
                    String description = weather.get(0).getDescription();
                    String CurrentCity = weather_response.getName();
                    int tempFully = (int) weather_response.getMain().getTemp();

                    int humidity = weather_response.getMain().getHumidity();
                    int feelsLike = (int) weather_response.getMain().getFeelsLike();

                    SimpleDateFormat currentDate = new SimpleDateFormat("h:mm a", Locale.getDefault());

                    String sunset = currentDate.format(weather_response.getSys().getSunset());
                    // String sunrise = currentDate.format(weather_response.getSys().getSunrise());
                    String country = weather_response.getSys().getCountry();

                    int windSpeed = (int) weather_response.getWind().getSpeed();

                    //set loaded data
                    weatherDescriptionTv.setText(description);
                    cityTv.setText(CurrentCity);
                    tempTv.setText(String.valueOf(tempFully + "\u00B0"));


                    humidityTv.setText(String.valueOf(humidity + " %"));
                    feelsLikeTv.setText(String.valueOf(feelsLike + "\u00B0"));

                    countryTv.setText(String.valueOf(country));
                    sunsetTv.setText(String.valueOf(sunset));

                    windTv.setText(String.valueOf(windSpeed + " m/s"));
                    pressureTv.setText(String.valueOf(weather_response.getMain().getPressure() + " hPa"));

                    windGust.setText(String.valueOf(weather_response.getWind().getGust() + " m/s"));
                    visibilityTv.setText(String.valueOf(weather_response.getVisibility() + " m"));


                    //change background
                    if (weather_response.getWeather().get(0).getDescription().contains("sky")) {
                        background.setBackgroundResource(R.drawable.back_main_sunny);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("clouds")) {
                        background.setBackgroundResource(R.drawable.back_main_cloudy);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("rain") || weather_response.getWeather().get(0).getDescription().contains("drizzle")) {
                        background.setBackgroundResource(R.drawable.back_main_rain);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("thunderstorm")) {
                        background.setBackgroundResource(R.drawable.back_main_thunder);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("snow") || weather_response.getWeather().get(0).getDescription().contains("shower")) {
                        background.setBackgroundResource(R.drawable.back_main);
                    } else if (weather_response.getWeather().get(0).getDescription().contains("mist") || weather_response.getWeather().get(0).getDescription().contains("fog")) {
                        background.setBackgroundResource(R.drawable.back_main_mist);
                    } else {
                        //default
                        background.setBackgroundResource(R.drawable.back_main);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(requireContext(), "internet wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getInfoAndInitForecastRecycler() {

        viewModel.getLiveForecastRequest().observe(getViewLifecycleOwner(), new Observer<Forecast_Request>() {
            @Override
            public void onChanged(Forecast_Request forecast_request) {
                try {

                    ArrayList<List_Info> list = new ArrayList<>(forecast_request.getList());

                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapter.setList(list);
                    recyclerView.setAdapter(adapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                            LinearLayoutManager.VERTICAL);
                    dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_line)));
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    additionalLineToRecycler.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("forecastError", String.valueOf(e));
                }
            }
        });


    }

    private void NetworkDisableAction() {

        viewModel.getIsNetworkErrorExist().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean state) {
                if (state) {
                    instructionLay.setVisibility(View.VISIBLE);
                    changeCurrentTown.setEnabled(false);
                    checkYourInternetConnection.setVisibility(View.VISIBLE);
                    skipBtn.setVisibility(View.GONE);
                    viewInstruction.setVisibility(View.GONE);
                } else {
                    instructionLay.setVisibility(View.GONE);
                    viewInstruction.setVisibility(View.GONE);
                    skipBtn.setVisibility(View.GONE);
                    changeCurrentTown.setEnabled(true);
                    checkYourInternetConnection.setVisibility(View.GONE);

                    isUserSawInstructions = sharedPreferences.getBoolean("checkInstructions", false);
                    instructionAction();
                }

            }
        });

    }

    //listen for internet on
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {

                    loadWeather(city);

                    Log.d("Internet_Log", "We have internet connection. Good to go.");
                } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                    Log.d("Internet_Log", "We have lost internet connection");
                }
            }
        }
    };

    private void broadCastWorker() {
        //from worker
        BroadcastReceiver broadcastReceiver = new WeatherUpdateReceiver();
        requireContext().registerReceiver(broadcastReceiver, new IntentFilter("WeatherCourse_Update"));
    }

    private void broadCastInternet() {
        //internet
        requireActivity().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}