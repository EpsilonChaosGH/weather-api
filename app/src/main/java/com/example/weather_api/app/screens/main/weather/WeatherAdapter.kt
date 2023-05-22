package com.example.weather_api.app.screens.main.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_api.app.model.ForecastState
import com.example.weather_api.databinding.ItemWeatherForecastBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    class WeatherViewHolder(
        val binding: ItemWeatherForecastBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var weatherList = listOf<ForecastState>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherForecastBinding.inflate(inflater, parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]

        with(holder.binding) {

            holder.binding.timeTextView.text = weather.data
            holder.binding.temperatureTextView.text = weather.temperature

            imageView.setImageResource(weather.weatherType.iconResId)
        }
    }

    override fun getItemCount() = weatherList.size
}