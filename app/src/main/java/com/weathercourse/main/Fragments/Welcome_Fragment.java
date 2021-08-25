package com.weathercourse.main.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weathercourse.main.Database_Room.Log_Model;
import com.weathercourse.main.Di.Common_AppModule;
import com.weathercourse.main.Di.DaggerCommon_AppComponent;
import com.weathercourse.main.Di.ViewModelFactory;
import com.weathercourse.main.R;
import com.weathercourse.main.Weather_ViewModel;
import com.weathercourse.main.databinding.FragmentWelcomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class Welcome_Fragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Inject
    SharedPreferences sharedPreferences;


    // TODO: Rename and change types and number of parameters
    public static Welcome_Fragment newInstance(String param1, String param2) {
        Welcome_Fragment fragment = new Welcome_Fragment();
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
                R.layout.fragment_welcome,
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DaggerCommon_AppComponent.builder().common_AppModule(new Common_AppModule(requireActivity().getApplication())).build().inject(this);

        String currentCityCheck = sharedPreferences.getString("currentCity", "NoSelected");

        if(currentCityCheck.equals("NoSelected")){

            binding.button.setVisibility(View.VISIBLE);
            binding.button.animate().alpha(1).setDuration(1000);

            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(view).navigate(R.id.action_welcome_Fragment_to_requestsFragment);
                }
            });
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(view).navigate(R.id.action_welcome_Fragment_to_main_Fragment);
                }
            },1000);
        }

    }
}