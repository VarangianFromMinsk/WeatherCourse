package com.weathercourse.main.Di;

import com.weathercourse.main.Fragments.Fragment_UpdateLog;
import com.weathercourse.main.Fragments.GeoPosition_Fragment;
import com.weathercourse.main.Fragments.Main_Fragment;
import com.weathercourse.main.Fragments.Welcome_Fragment;
import com.weathercourse.main.MainActivity;
import com.weathercourse.main.Services.Work_UpdateByInterval;
import com.weathercourse.main.Weather_ViewModel;
import com.weathercourse.main.Widget.WeatherWidget;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {Common_AppModule.class,ViewModelModule.class})
public interface Common_AppComponent  {

    void inject(Weather_ViewModel weather_viewModel);

    void inject(Main_Fragment main_fragment);

    void inject(GeoPosition_Fragment geoPosition_fragment);

    void inject(Welcome_Fragment welcome_fragment);

    void inject(Fragment_UpdateLog fragment_updateLog);

    void inject(Work_UpdateByInterval workUpdateByInterval);

    void inject(WeatherWidget weatherWidget);

    void inject(MainActivity mainActivity);
}
