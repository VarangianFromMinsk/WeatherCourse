package com.weathercourse.main.Di;

import android.app.Application;

public class App extends Application {

    private Common_AppComponent commonComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        commonComponent = DaggerCommon_AppComponent.builder()
                .common_AppModule(new Common_AppModule(this))
                .build();
    }

    public Common_AppComponent getCommonComponent() {
        return commonComponent;
    }
}
