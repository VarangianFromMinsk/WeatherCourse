package com.weathercourse.main.Fragments;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.weathercourse.main.Di.Common_AppModule;
import com.weathercourse.main.Di.DaggerCommon_AppComponent;
import com.weathercourse.main.Di.ViewModelFactory;
import com.weathercourse.main.Weather_ViewModel;
import com.weathercourse.main.R;
import com.weathercourse.main.databinding.FragmentGeoPositionBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;


public class GeoPosition_Fragment extends Fragment {

    private FragmentGeoPositionBinding binding;
    private LocationManager locationManager;
    private LocationListener mlocListener;
    private boolean isGeoPositionFind;
    private View useView;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ViewModelFactory viewModelFactory;
    Weather_ViewModel viewModel;

    // TODO: Rename and change types and number of parameters
    public static GeoPosition_Fragment newInstance(String param1, String param2) {
        GeoPosition_Fragment fragment = new GeoPosition_Fragment();
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
                R.layout.fragment_geo_position,
                container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialization(view);

        initAnimationOfLoad();

        createLocationListener();

        findOutGeoPositionAction();

        observeChanging(view);
    }

    private void initialization(View view) {
        DaggerCommon_AppComponent.builder().common_AppModule(new Common_AppModule(requireActivity().getApplication())).build().inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(Weather_ViewModel.class);

        useView = view;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.textViewEditCityByHandle.setVisibility(View.VISIBLE);
            }
        },3500);
    }

    private void initAnimationOfLoad() {
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.imageLoading.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.imageLoading.startAnimation(animation);
    }

    private void createLocationListener() {
        mlocListener = new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    Geocoder geoCoder = new Geocoder(requireContext(), Locale.ENGLISH);
                    List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String city = address.get(0).getLocality();
                    Toast.makeText(requireContext(), city, Toast.LENGTH_SHORT).show();
                    viewModel.setCurrentTown(city);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.imageLoading.setVisibility(View.GONE);

                } catch (IOException e) {
                    locationManager.removeUpdates(mlocListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                    e.printStackTrace();
                }
            }
        };
    }


    private void findOutGeoPositionAction() {

        //additional check
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Oops, something went wrong...", Toast.LENGTH_SHORT).show();
            return;
        }


        locationManager = (LocationManager) requireActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        binding.textViewEditCityByHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.GONE);
                binding.imageLoading.setVisibility(View.GONE);

                binding.yourCityEdit.setVisibility(View.VISIBLE);
                binding.editYourCiryBtn.setVisibility(View.VISIBLE);

                binding.editYourCiryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Objects.requireNonNull(binding.yourCityEdit.getText()).toString().trim().length() < 2) {
                            Toast.makeText(requireContext(), "This city don't exist", Toast.LENGTH_LONG).show();
                        } else if (binding.yourCityEdit.getText().toString().trim().isEmpty()) {
                            Toast.makeText(requireContext(), "Please,enter your city name", Toast.LENGTH_LONG).show();
                        } else {
                            binding.yourTownTv.setText(binding.yourCityEdit.getText().toString().trim());
                            sharedPreferences.edit().putString("currentCity", binding.yourCityEdit.getText().toString().trim()).apply();
                            isGeoPositionFind = true;

                            locationManager.removeUpdates(mlocListener);

                            Navigation.findNavController(useView).navigate(R.id.action_geoPosition_Fragment_to_main_Fragment);
                        }
                    }
                });
            }
        });

        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
        } catch (Exception e) {
            Log.d("gpsError", String.valueOf(e));
        }
    }

    private void observeChanging(View view) {
        viewModel.getCurrentTown().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String city) {
                binding.yourTownTv.setText(city);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isGeoPositionFind){
                            sharedPreferences.edit().putString("currentCity", city).apply();
                            locationManager.removeUpdates(mlocListener);
                            Navigation.findNavController(view).navigate(R.id.action_geoPosition_Fragment_to_main_Fragment);
                        }
                    }
                }, 2000);
            }
        });
    }

}