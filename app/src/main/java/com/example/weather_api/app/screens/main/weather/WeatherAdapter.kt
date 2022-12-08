package com.example.weather_api.app.screens.main.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_api.R
import com.example.weather_api.app.model.main.entities.WeatherEntity
import com.example.weather_api.databinding.ItemWeatherForecastBinding
import java.text.SimpleDateFormat
import java.util.*
import java.sql.Date


class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(
        val binding: ItemWeatherForecastBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var weatherList = listOf<WeatherEntity>()
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

    @SuppressLint("SimpleDateFormat")
    private fun dataToTime(data: Date): String? {
        return try {
            val sdf = SimpleDateFormat("EEE, HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(data)
        } catch (e: Exception) {
            e.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]

        holder.binding.timeTextView.text = dataToTime(weather.data)
        holder.binding.temperatureTextView.text = "${weather.temperature}°"
        when (weather.mainWeather) {
            "Clear" -> holder.binding.imageView.setImageResource(R.drawable.ic_sun)
            "Clouds" -> holder.binding.imageView.setImageResource(R.drawable.ic_cloud)
            "Rain" -> holder.binding.imageView.setImageResource(R.drawable.ic_heavey_rain)
            "Snow" -> holder.binding.imageView.setImageResource(R.drawable.ic_winter)
            "Mist" -> holder.binding.imageView.setImageResource(R.drawable.ic_fog)
            else -> holder.binding.imageView.setImageResource(R.drawable.ic_cloudy)
        }
    }

    override fun getItemCount() = weatherList.size
}