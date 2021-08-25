package com.weathercourse.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.weathercourse.main.Di.Common_AppModule;
import com.weathercourse.main.Di.DaggerCommon_AppComponent;
import com.weathercourse.main.Di.ViewModelFactory;
import com.weathercourse.main.R;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

    }

}