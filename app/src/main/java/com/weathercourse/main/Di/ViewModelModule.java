package com.weathercourse.main.Di;

import androidx.lifecycle.ViewModel;

import com.weathercourse.main.Weather_ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(Weather_ViewModel.class)
    abstract ViewModel weatherViewModel(Weather_ViewModel weather_viewModel);

}
