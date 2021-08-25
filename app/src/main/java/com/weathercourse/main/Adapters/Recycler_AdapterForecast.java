package com.weathercourse.main.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.weathercourse.main.Services.Pojo_Forecast.List_Info;
import com.weathercourse.main.R;
import com.weathercourse.main.databinding.RowForecastBinding;
import com.weathercourse.main.databinding.RowForecastDayBinding;

import java.util.ArrayList;

public class Recycler_AdapterForecast extends RecyclerView.Adapter {

    private ArrayList<List_Info> list ;
    private Context context;

    public Recycler_AdapterForecast(Context context) {
        this.context = context;
    }

    public void setList(ArrayList<List_Info> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getDtTxt().contains("00:00:00")){
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            RowForecastDayBinding rowForecastDayBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.row_forecast_day,
                    parent, false);
            return new ViewHolderDay(rowForecastDayBinding);
        }
        else {
            RowForecastBinding rowForecastBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.row_forecast,
                    parent, false);
            return new ViewHolderMain(rowForecastBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        List_Info listInfo = list.get(position);

        String hour = listInfo.getDtTxt().substring(11,13);
        String day = listInfo.getDtTxt().substring(8,10);
        String month = listInfo.getDtTxt().substring(5,7);

        Log.d("timeCheck", day);

        if(holder instanceof ViewHolderDay){

            ((ViewHolderDay) holder).dayBinding.numberTv.setText(day);
            ((ViewHolderDay) holder).dayBinding.monthTv.setText(month);
        }
        else if(holder instanceof ViewHolderMain){

            ((ViewHolderMain) holder).forecastBinding.timeTv.setText(hour);
            ((ViewHolderMain) holder).forecastBinding.humidityTv.setText(String.valueOf(listInfo.getMain().getHumidity()));
            ((ViewHolderMain) holder).forecastBinding.tempRecuclerTv.setText(String.valueOf((int)listInfo.getMain().getTemp() + "\u00B0"));

            if(listInfo.getWeather().get(0).getDescription().contains("sky")){
                //clear sly
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_sky);
            }
            else if(listInfo.getWeather().get(0).getDescription().contains("clouds")){
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_cloudy);
            }
            else if(listInfo.getWeather().get(0).getDescription().contains("rain") || listInfo.getWeather().get(0).getDescription().contains("drizzle")){
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_rain);
            }
            else if(listInfo.getWeather().get(0).getDescription().contains("thunderstorm")){
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_thunder);
            }
            else if(listInfo.getWeather().get(0).getDescription().contains("snow") || listInfo.getWeather().get(0).getDescription().contains("shower")){
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_snow);
            }
            else if(listInfo.getWeather().get(0).getDescription().contains("mist") || listInfo.getWeather().get(0).getDescription().contains("fog")){
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_fog);
            }
            else{
                //default
                ((ViewHolderMain) holder).forecastBinding.imageView.setImageResource(R.drawable.weather_default);
            }
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class ViewHolderMain extends RecyclerView.ViewHolder {

        private final RowForecastBinding forecastBinding;

        public ViewHolderMain(RowForecastBinding forecastBinding) {
            super(forecastBinding.getRoot());
            this.forecastBinding = forecastBinding;
        }

    }


    private static class ViewHolderDay extends RecyclerView.ViewHolder {

        private final RowForecastDayBinding dayBinding;

        public ViewHolderDay(RowForecastDayBinding rowForecastDayBinding) {
            super(rowForecastDayBinding.getRoot());
            this.dayBinding = rowForecastDayBinding;
        }

    }
}
